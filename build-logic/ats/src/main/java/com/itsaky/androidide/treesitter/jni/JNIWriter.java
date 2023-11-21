/*
 *  This file is part of android-tree-sitter.
 *
 *  android-tree-sitter library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  android-tree-sitter library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *  along with android-tree-sitter.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.itsaky.androidide.treesitter.jni;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import kotlin.Triple;

/**
 * @author Akash Yadav
 */
public class JNIWriter {

  private static final boolean isWindows = System.getProperty("os.name").startsWith("Windows");

  /* Signature Characters */
  private static final String SIG_VOID = "V";
  private static final String SIG_BOOLEAN = "Z";
  private static final String SIG_BYTE = "B";
  private static final String SIG_CHAR = "C";
  private static final String SIG_SHORT = "S";
  private static final String SIG_INT = "I";
  private static final String SIG_LONG = "J";
  private static final String SIG_FLOAT = "F";
  private static final String SIG_DOUBLE = "D";
  private static final String SIG_ARRAY = "[";
  private static final String SIG_CLASS = "L";


  private final Types types;
  private final TypeMirror stringType;
  private final TypeMirror throwableType;
  private final TypeMirror classType;

  public JNIWriter(Types types, Elements elements) {
    this.types = types;

    this.stringType = elements.getTypeElement("java.lang.String").asType();
    this.throwableType = elements.getTypeElement("java.lang.Throwable").asType();
    this.classType = elements.getTypeElement("java.lang.Class").asType();
  }

  static boolean isStatic(Element s) {
    return hasFlag(s, Modifier.STATIC);
  }

  static boolean isFinal(Element s) {
    return hasFlag(s, Modifier.FINAL);
  }

  static boolean isNative(Element s) {
    return hasFlag(s, Modifier.NATIVE);
  }

  static boolean hasFlag(Element s, Modifier m) {
    return s.getModifiers().contains(m);
  }

  static boolean isLocalType(TypeElement typeElement) {
    Element enclosingElement = typeElement.getEnclosingElement();

    while (enclosingElement != null) {
      if (enclosingElement instanceof TypeElement) {
        // Continue checking up the hierarchy
        enclosingElement = ((TypeElement) enclosingElement).getEnclosingElement();
      } else if (enclosingElement instanceof javax.lang.model.element.VariableElement ||
        enclosingElement instanceof javax.lang.model.element.ExecutableElement) {
        // Found a method or variable initializer, so the type is local
        return true;
      } else {
        // Reached a top-level element (class, package, etc.)
        break;
      }
    }

    // If we reach here, the type is not local
    return false;
  }

  static TypeElement outermostClass(TypeElement c) {
    Element sym = c;
    Element prev = null;
    while (sym.getKind() != ElementKind.PACKAGE) {
      prev = sym;
      sym = sym.getEnclosingElement();
    }
    return (TypeElement) prev;
  }

  public boolean needsHeader(TypeElement c) {
    if (isLocalType(c)) {
      return false;
    }

    return needsHeader(outermostClass(c), true);
  }

  private boolean needsHeader(TypeElement c, boolean checkNestedClasses) {
    if (isLocalType(c)) {
      return false;
    }

    for (Element sym : c.getEnclosedElements()) {
      if (sym.getKind() == ElementKind.METHOD && isNative(sym)) {
        return true;
      }
    }

    if (checkNestedClasses) {
      for (Element sym : c.getEnclosedElements()) {
        if ((sym.getKind().isClass()) && needsHeader(((TypeElement) sym), true)) {
          return true;
        }
      }
    }
    return false;
  }

  public JNIWriterResult generate(TypeElement type) {
    final var _writer = new StringWriter();
    final var writer = new PrintWriter(_writer);

    final var cname = encode(type.getQualifiedName(), EncoderType.CLASS);
    // Format: [ParentClass_]NestedClass_
    final var typeName = new StringBuilder();

    // Format: [com/my/package][/ParentOrCurrentClass][$CurrentClassIfNested]
    // This is a bytecode-type reference name
    // e.g. for "com.itsaky.androidide.treesitter.TreeSitter.Native
    // this will be "com/itsaky/androidide/treesitter/TreeSitter$Native
    final var typeRef = new StringBuilder();

    buildTypeNames(type, typeName, typeRef);

    final var sigs = new HashMap<String, String>();

    // Write native headers file
    fileTop(writer);
    includes(writer);
    guardBegin(writer, cname, "METHODS");

    cppGuardBegin(writer);

    writeStatics(writer, type);
    writeMethods(writer, type, cname, sigs);

    cppGuardEnd(writer); // __cplusplus

    guardBegin(writer, cname, "METHOD_SIGNATURES");
    writeMethodMeta(writer, cname, type, typeName, typeRef, sigs);
    guardEnd(writer); // METHOD_SIGNATURES

    guardEnd(writer); // METHODS

    return new JNIWriterResult(cname, typeName, typeRef, "", _writer.toString());
  }

  public static String generateJNIOnLoadHeader(List<JNIWriterResult> results) {
    final var _writer = new StringWriter();
    final var writer = new PrintWriter(_writer);

    // Remove duplicates
    results = new ArrayList<>(results.stream()
      .collect(Collectors.toMap(JNIWriterResult::getHeaderFilename, Function.identity(),
        (existing, replacement) -> existing))
      .values());

    fileTop(writer);
    includes(writer);

    // Add '#include "filename" for all generated files
    writer.println();
    writer.println(String.join(System.lineSeparator(),
      results.stream().map(result -> "#include \"" + result.getHeaderFilename() + "\"").toList()));
    writer.println();
    writer.println();

    guardBegin(writer, "_TS_JNI_ONLOAD", "");

    writer.println("#define TS_JNI_ONLOAD__AUTO_REGISTER(env) \\");

    for (final var result : results) {
      final var typeName = result.getTypeName();
      writer.print("    ");
      writer.print(typeName_AutoRegisterNatives(typeName));
      writer.println("(env); \\");
    }

    writer.println();

    writer.println("#define TS_JNI_ONLOAD__DEFINE_METHODS_ARR \\");

    for (final var result : results) {
      final var typeName = result.getTypeName();
      writer.print("    ");
      writer.print(typeName_DefMethodsArray(typeName));
      writer.println("; \\");
    }

    writer.println();

    guardEnd(writer); // _TS_JNI_ONLOAD

    return _writer.toString();
  }

  private void writeMethodMeta(PrintWriter out, String cname, TypeElement type,
                               StringBuilder typeName, StringBuilder typeRef,
                               HashMap<String, String> sigs
  ) {
    final var defTypMth = typeName + "_METHODS";
    final var defTypMthCount = typeName + "_METHOD_COUNT";
    final var typeName__SetJniMethods = typeName_SetJniMethods(typeName);
    final var typeName__AutoRegisterNatives = typeName_AutoRegisterNatives(typeName);
    final var typeName__DefMethodsArray = typeName_DefMethodsArray(typeName);
    final var nameAndSigList = new ArrayList<Triple<String, String, String>>();

    // In Android, the JNINativeMethod structure has 'const' properties
    // While in JDK, it is not
    // So we disable the warning when compiling for non-Android
    jvmDisableWarning(out, "write-strings", () -> {
      var idx = 0;
      for (final var entry : sigs.entrySet()) {
        final var methodName = entry.getKey();
        final var methodSig = entry.getValue();

        final var qualifiedMethodName = typeName + methodName;

        out.println();

        // Write index of method in the JNINativeMethod[]
        final var defArrIdx = qualifiedMethodName + "__ARR_IDX";
        out.print("// Index of method ");
        out.print(methodName);
        out.print(" of class ");
        out.print(cname);
        out.print(" in ");
        out.print(defTypMth);
        out.println(" array");
        out.print("#define ");
        out.print(defArrIdx);
        out.print(" ");
        out.println(idx++);

        // Write JNINativeMethod definition

        writeJniNativeMethod(out, cname, methodName, methodSig, qualifiedMethodName);

        // record info about this method
        // used later for definition JNINativeMethod[] and method count
        nameAndSigList.add(new Triple<>(qualifiedMethodName, methodName, methodSig));
      }
      out.println();
    });

    writeSetJniMethods(out, typeRef, typeName__SetJniMethods);

    // Write method count
    out.println();
    out.print("// Number of elements in ");
    out.println(defTypMth);
    out.print("#define ");
    out.print(defTypMthCount);
    out.print(" ");
    out.println(nameAndSigList.size());

    writeJniNativeMethodArr(
      out, defTypMth, typeName__DefMethodsArray, typeRef, nameAndSigList
    );

    // Writer SET_JNI_METHOD macro
    writeJniNativeMethodSetter(out, defTypMth);

    // typeName_AutoRegisterNatives(env) macro
    writeRegisterNativesHelpers(
      out,
      typeName,
      typeRef,
      defTypMth,
      defTypMthCount,
      typeName__SetJniMethods,
      typeName__AutoRegisterNatives,
      nameAndSigList
    );

    out.println();
  }

  private static String typeName_SetJniMethods(CharSequence typeName) {
    return typeName + "_SetJniMethods";
  }

  private static String typeName_AutoRegisterNatives(CharSequence typeName) {
    return typeName + "_AutoRegisterNatives";
  }

  private static String typeName_DefMethodsArray(CharSequence typeName) {
    return typeName + "_DefMethodsArray";
  }

  private static void writeSetJniMethods(PrintWriter out, StringBuilder typeRef,
                                         String typeName__SetJniMethods
  ) {
    out.println();
    out.println("// This function is called in order to set the actual");
    out.println("// implementations of the native methods defined in class ");
    out.print("// ");
    out.println(typeRef);
    out.print("void ");
    out.print(typeName__SetJniMethods);
    out.println("(JNINativeMethod *methods, int count);");
  }

  private static void buildTypeNames(TypeElement type, StringBuilder typeName, StringBuilder typeRef
  ) {
    Element curr = type;
    while (curr != null && curr.getKind().isClass()) {
      var parent = curr.getEnclosingElement();
      typeName.insert(0, "_").insert(0, curr.getSimpleName());
      typeRef.insert(0, curr.getSimpleName());

      if (parent.getKind() == ElementKind.PACKAGE) {
        typeRef.insert(0, "/");
        typeRef.insert(0,
          ((PackageElement) parent).getQualifiedName().toString().replace('.', '/'));
      } else if (parent.getKind().isClass()) {
        typeRef.insert(0, "$");
      }

      curr = parent;
    }
  }

  private static void writeJniNativeMethod(
    PrintWriter out,
    String cname,
    String methodName,
    String methodSig,
    String qualifiedMethodName
  ) {
    writeMethodDoc(out, cname, methodName, methodSig);
    out.print("static JNINativeMethod ");
    out.print(qualifiedMethodName);
    out.println("= {");
    out.print("    .name = \"");
    out.print(methodName);
    out.println("\",");
    out.print("    .signature = \"");
    out.print(methodSig);
    out.println("\",");
    out.println("    .fnPtr = nullptr");
    out.println("};");
  }

  private static void writeJniNativeMethodArr(
    PrintWriter out,
    String defTypMth,
    String typeName__DefMethodsArray,
    StringBuilder typeRef,
    ArrayList<Triple<String, String, String>> nameAndSigList
  ) {
    out.print("// All native methods in class ");
    out.println(typeRef);

    out.print("#define ");
    out.print(typeName__DefMethodsArray);
    out.println(" \\");
    out.print("    JNINativeMethod ");
    out.print(defTypMth);
    out.println("[] = { \\");

    for (Triple<String, String, String> pair : nameAndSigList) {
      out.print("        ");
      out.print(pair.getFirst());
      out.println(", \\");
    }

    out.println("    };");
  }

  private static void writeJniNativeMethodSetter(PrintWriter out, String defTypMth) {
    out.println();
    out.println("#ifndef SET_JNI_METHOD");
    out.println("#define SET_JNI_METHOD(_mths, _mth, _func) { \\");
    out.println("    void *ptr = (void *)(&_func); \\");
    out.println("    (_mths + _mth##__ARR_IDX)->fnPtr = ptr; \\");
    ifLoggingEnabled(out, true, true, () -> {
      out.println("    LOGD(\"AndroidTreeSitter\", \"SET_JNI_METHOD: %s to %p\", _mth.name, ptr); \\");
      out.println("    LOGD(\"AndroidTreeSitter\", \"SET_JNI_METHOD: fnPtr = %p\", _mths[_mth##__ARR_IDX].fnPtr); \\");
    });
    out.println("}");
    out.print("#endif");
    out.println();
  }

  private static void writeRegisterNativesHelpers(PrintWriter out, StringBuilder typeName,
                                                  StringBuilder typeRef, String defTypMth,
                                                  String defTypMthCount,
                                                  String typeName__SetJniMethods,
                                                  String typeName__AutoRegisterNatives,
                                                  ArrayList<Triple<String, String, String>> nameAndSigList
  ) {
    final var typeName_class = typeName + "_class";

    out.println();
    out.print("// Registers the native methods of class ");
    out.print(typeRef);
    out.println(" with the given JNIEnv");
    out.print("#define ");
    out.print(typeName__AutoRegisterNatives);
    out.println("(_env) \\");

    out.print("    ");
    out.print(typeName__SetJniMethods);
    out.print("(&");
    out.print(defTypMth);
    out.print("[0], ");
    out.print(defTypMthCount);
    out.println("); \\");

    ifLoggingEnabled(out, true, true, () -> {
      out.print("    for (int i = 0; i < ");
      out.print(defTypMthCount);
      out.print("; ++i) { JNINativeMethod mth = *(");
      out.print(defTypMth);
      out.println(" + i); LOGD(\"AndroidTreeSitter\", \"Register native method: '%s', '%s', '%p'\", mth.name, mth.signature, mth.fnPtr); } \\");
    });

    out.print("    jclass ");
    out.print(typeName_class);
    out.print(" = (*env).FindClass(\"");
    out.print(typeRef);
    out.println("\"); \\");
    out.print("    (*_env).RegisterNatives(");
    out.print(typeName_class);
    out.print(", ");
    out.print(defTypMth);
    out.print(", ");
    out.print(defTypMthCount);
    out.println("); \\");

//    out.print("free(");
//    out.print(defTypMth);
//    out.println(")");

    out.println();
  }

  private static void jvmDisableWarning(PrintWriter out, String name, Runnable doWrite) {
    out.println();
    out.println("#ifndef __ANDROID__");
    out.println();
    out.println("#ifdef __GNUC__");
    out.println("#pragma GCC diagnostic push");
    out.print("#pragma GCC diagnostic ignored \"-W");
    out.print(name);
    out.println("\"");
    out.println("#endif // __GNUC__");
    out.println();
    out.println("#ifdef __clang__");
    out.println("#pragma clang diagnostic push");
    out.print("#pragma clang diagnostic ignored \"-W");
    out.print(name);
    out.println("\"");
    out.println("#endif // __clang__");
    out.println();
    out.println("#endif // __ANDROID__");
    out.println();

    doWrite.run();

    out.println();
    out.println("#ifndef __ANDROID__");
    out.println();
    out.println("#ifdef __GNUC__");
    out.println("#pragma GCC diagnostic pop");
    out.println("#endif // __GNUC__");
    out.println();
    out.println("#ifdef __clang__");
    out.println("#pragma clang diagnostic pop");
    out.println("#endif // __clang__");
    out.println();
    out.println("#endif // __ANDROID__");
    out.println();
  }

  private static void ifLoggingEnabled(PrintWriter out, boolean macroEscape, boolean macroEscapeOnClose, Runnable runnable) {
    out.print("if (__TS_LOG_DEBUG == 1) {");
    out.println(macroEscape ? " \\" : "");
    runnable.run();
    out.print("}");
    out.println(macroEscape && macroEscapeOnClose ? " \\" : "");
  }

  protected void writeStatics(PrintWriter out, TypeElement sym) {
    List<TypeElement> clist = new ArrayList<>();
    for (TypeElement cd = sym; cd != null; cd = (TypeElement) types.asElement(cd.getSuperclass())) {
      clist.add(cd);
    }
    /*
     * list needs to be super-class, base-class1, base-class2 and so on,
     * so we reverse class hierarchy
     */
    Collections.reverse(clist);
    for (TypeElement cd : clist) {
      for (Element i : cd.getEnclosedElements()) {
        // consider only final, static and fields with ConstantExpressions
        if (isFinal(i) && isStatic(i) && i.getKind().isField()) {
          VariableElement v = (VariableElement) i;
          if (v.getConstantValue() != null) {
            Pair<TypeElement, VariableElement> p = new Pair<>(sym, v);
            printStaticDefines(out, p);
          }
        }
      }
    }
  }

  static void printStaticDefines(PrintWriter out, Pair<TypeElement, VariableElement> p) {
    TypeElement cls = p.first;
    VariableElement f = p.second;
    Object value = f.getConstantValue();
    String valueStr = null;
    switch (f.asType().getKind()) {
      case BOOLEAN:
        valueStr = (((Boolean) value) ? "1L" : "0L");
        break;
      case BYTE:
      case SHORT:
      case INT:
        valueStr = value.toString() + "L";
        break;
      case LONG:
        // Visual C++ supports the i64 suffix, not LL.
        valueStr = value.toString() + ((isWindows) ? "i64" : "LL");
        break;
      case CHAR:
        Character ch = (Character) value;
        valueStr = String.valueOf(((int) ch) & 0xffff) + "L";
        break;
      case FLOAT:
        // bug compatible
        float fv = ((Float) value).floatValue();
        valueStr = (Float.isInfinite(fv)) ? ((fv < 0) ? "-" : "") + "Inff" : value.toString() + "f";
        break;
      case DOUBLE:
        // bug compatible
        double d = ((Double) value).doubleValue();
        valueStr = (Double.isInfinite(d)) ? ((d < 0) ? "-" : "") + "InfD" : value.toString();
        break;
      default:
        valueStr = null;
    }
    if (valueStr != null) {
      out.print("#undef ");
      String cname = encode(cls.getQualifiedName(), EncoderType.CLASS);
      String fname = encode(f.getSimpleName(), EncoderType.FIELDSTUB);
      out.println(cname + "_" + fname);
      out.print("#define " + cname + "_");
      out.println(fname + " " + valueStr);
    }
  }

  void writeMethods(PrintWriter out, TypeElement sym, String cname, Map<String, String> methodSigs
  ) {
    List<? extends Element> classmethods = sym.getEnclosedElements();

    for (Element e : classmethods) {
      if (e.getKind() != ElementKind.METHOD) {
        continue;
      }

      ExecutableElement md = ((ExecutableElement) e);

      if (!isNative(md)) {
        continue;
      }

      // validate the method

      TypeSignature newtypesig = new TypeSignature(types);
      CharSequence methodName = md.getSimpleName();

      boolean isOverloaded = false;
      for (Element md2 : classmethods) {
        if ((md2 != md) && (methodName.equals(md2.getSimpleName())) && isNative(md2)) {
          isOverloaded = true;
        }
      }

      final var methodSig = newtypesig.getSignature(md);
      methodSigs.put(methodName.toString(), methodSig.toString());

      writeMethodDoc(out, cname, methodName, methodSig);
      out.println("JNIEXPORT " + jniType(types.erasure(md.getReturnType())) + " JNICALL " +
        encodeMethod(md, sym, isOverloaded));

      final var criticalNative = md.getAnnotationMirrors()
        .stream()
        .filter(am -> ((TypeElement) am.getAnnotationType().asElement()).getQualifiedName()
          .contentEquals("dalvik.annotation.optimization.CriticalNative"))
        .findFirst();

      if (criticalNative.isPresent()) {
        // omit JNIEnv and jclass from @CriticalNative methods
        out.print("  (");
      } else {
        out.print("  (JNIEnv *env, ");
        out.print((isStatic(md)) ? "jclass clazz" : "jobject self");
      }

      for (VariableElement v : md.getParameters()) {
        var arg = types.erasure(v.asType());
        out.print(", ");
        out.print(jniType(arg));
        out.print(" ");
        out.print(v.getSimpleName());
      }
      out.println(");");
      out.println();
    }
  }

  private static void writeMethodDoc(PrintWriter out, String cname, CharSequence methodName,
                                     CharSequence methodSig
  ) {
    out.println("/*");
    out.println(" * Class:     " + cname);
    out.println(" * Method:    " + encode(methodName, EncoderType.FIELDSTUB));
    out.println(" * Signature: " + methodSig);
    out.println(" */");
  }

  String encodeMethod(ExecutableElement msym, TypeElement clazz, boolean isOverloaded) {
    StringBuilder result = new StringBuilder(100);
    result.append("Java_");

    /* JNI */
    result.append(encode(clazz.getQualifiedName(), EncoderType.JNI));
    result.append('_');
    result.append(encode(msym.getSimpleName(), EncoderType.JNI, false));

    if (isOverloaded) {
      TypeSignature typeSig = new TypeSignature(types);
      StringBuilder sig = typeSig.getParameterSignature(msym, true);
      result.append("__").append(encode(sig, EncoderType.JNI));
    }
    return result.toString();
  }

  @SuppressWarnings("fallthrough")
  protected final String jniType(TypeMirror t) {
    var type = switch (t.getKind()) {
      case ARRAY -> switch (((ArrayType) t).getComponentType().getKind()) {
        case BOOLEAN -> "jbooleanArray";
        case BYTE -> "jbyteArray";
        case CHAR -> "jcharArray";
        case SHORT -> "jshortArray";
        case INT -> "jintArray";
        case LONG -> "jlongArray";
        case FLOAT -> "jfloatArray";
        case DOUBLE -> "jdoubleArray";
        case ARRAY, DECLARED -> "jobjectArray";
        default -> throw new Error(((ArrayType) t).getComponentType().toString());
      };
      case VOID -> "void";
      case BOOLEAN -> "jboolean";
      case BYTE -> "jbyte";
      case CHAR -> "jchar";
      case SHORT -> "jshort";
      case INT -> "jint";
      case LONG -> "jlong";
      case FLOAT -> "jfloat";
      case DOUBLE -> "jdouble";
      default -> null;
    };

    if (type != null) {
      return type;
    }

    if (t.getKind() != TypeKind.DECLARED) {
      throw new IllegalArgumentException("jni unknown type: " + t);
    }

    if (types.isAssignable(t, stringType)) {
      return "jstring";
    } else if (types.isAssignable(t, throwableType)) {
      return "jthrowable";
    } else if (types.isAssignable(t, classType)) {
      return "jclass";
    } else {
      return "jobject";
    }
  }

  protected static void fileTop(PrintWriter out) {
    out.println("/* DO NOT EDIT THIS FILE - it is machine generated */");
  }

  protected static void includes(PrintWriter out) {
    out.println("#include <jni.h>");
    out.println("#include \"utils/ts_header_conf.h\"");
  }

  /*
   * Deal with the C pre-processor.
   */
  protected static void cppGuardBegin(PrintWriter out) {
    out.println("#ifdef __cplusplus");
    out.println("extern \"C\" {");
    out.println("#endif");
  }

  protected static void cppGuardEnd(PrintWriter out) {
    out.println("#ifdef __cplusplus");
    out.println("}");
    out.println("#endif");
  }

  protected static void guardBegin(PrintWriter out, String cname, String type) {
    out.println();
    String guardName = ("_Included_" + cname + "_" + type).toUpperCase(Locale.ROOT);
    out.print("#ifndef ");
    out.println(guardName);

    out.print("#define ");
    out.println(guardName);
  }

  protected static void guardEnd(PrintWriter out) {
    out.println("#endif");
  }

  enum EncoderType {
    CLASS, FIELDSTUB, FIELD, JNI, SIGNATURE
  }

  static String encode(CharSequence name, EncoderType type) {
    return encode(name, type, false);
  }

  @SuppressWarnings("fallthrough")
  static String encode(CharSequence name, EncoderType mtype, boolean undScrToUndScr1) {
    StringBuilder result = new StringBuilder(100);
    int length = name.length();

    for (int i = 0; i < length; i++) {
      char ch = name.charAt(i);
      if (isalnum(ch)) {
        result.append(ch);
        continue;
      }
      switch (mtype) {
        case CLASS -> {
          switch (ch) {
            case '.', '_' -> result.append("_");
            case '$' -> result.append("__");
            default -> result.append(encodeChar(ch));
          }
        }
        case JNI -> {
          switch (ch) {
            case '/', '.' -> result.append("_");
            case '_' -> {
              result.append("_");
              if (undScrToUndScr1) {
                result.append(1);
              }
            }
            case ';' -> result.append("_2");
            case '[' -> result.append("_3");
            default -> result.append(encodeChar(ch));
          }
        }
        case SIGNATURE -> result.append(isprint(ch) ? ch : encodeChar(ch));
        case FIELDSTUB -> result.append(ch == '_' ? ch : encodeChar(ch));
        default -> result.append(encodeChar(ch));
      }
    }
    return result.toString();
  }

  static String encodeChar(char ch) {
    String s = Integer.toHexString(ch);
    int nzeros = 5 - s.length();
    char[] result = new char[6];
    result[0] = '_';
    for (int i = 1; i <= nzeros; i++) {
      result[i] = '0';
    }
    for (int i = nzeros + 1, j = 0; i < 6; i++, j++) {
      result[i] = s.charAt(j);
    }
    return new String(result);
  }

  /* Warning: Intentional ASCII operation. */
  private static boolean isalnum(char ch) {
    return ch <= 0x7f && /* quick test */
      ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9'));
  }

  /* Warning: Intentional ASCII operation. */
  private static boolean isprint(char ch) {
    return ch >= 32 && ch <= 126;
  }

  private static class TypeSignature {

    static class SignatureException extends Exception {

      private static final long serialVersionUID = 1L;

      SignatureException(String reason) {
        super(reason);
      }
    }

    Elements elems;
    Types types;

    public TypeSignature(Types types) {
      this.types = types;
    }

    StringBuilder getParameterSignature(ExecutableElement mType, boolean useFlatname) {
      StringBuilder result = new StringBuilder();
      for (VariableElement v : mType.getParameters()) {
        result.append(getJvmSignature(v.asType(), useFlatname));
      }
      return result;
    }

    StringBuilder getReturnSignature(ExecutableElement mType) {
      return getJvmSignature(mType.getReturnType(), false);
    }

    StringBuilder getSignature(ExecutableElement mType) {
      StringBuilder sb = new StringBuilder();
      sb.append("(").append(getParameterSignature(mType, false)).append(")");
      sb.append(getReturnSignature(mType));
      return sb;
    }

    /*
     * Returns jvm internal signature.
     */
    static class JvmTypeVisitor extends SimpleTypeVisitor8<Type, StringBuilder> {

      private final Types types;

      JvmTypeVisitor(Types types) {
        super();
        this.types = types;
      }

      @Override
      public Type visitDeclared(DeclaredType t, StringBuilder stringBuilder) {
        setDeclaredType(t, stringBuilder);
        return null;
      }

      @Override
      public Type visitArray(ArrayType t, StringBuilder stringBuilder) {
        stringBuilder.append("[");
        return t.getComponentType().accept(this, stringBuilder);
      }

      @Override
      public Type visitPrimitive(PrimitiveType t, StringBuilder stringBuilder) {
        stringBuilder.append(getJvmPrimitiveSignature(t));
        return null;
      }

      private void setDeclaredType(TypeMirror t, StringBuilder s) {
        var tsym = ((TypeElement) types.asElement(t));
        String classname = tsym.getQualifiedName().toString();
        classname = classname.replace('.', '/');
        s.append("L").append(classname).append(";");
      }

      private String getJvmPrimitiveSignature(PrimitiveType t) {
        var sig = primitiveSigOrNull(t);
        if (sig != null) {
          return sig;
        }

        throw new IllegalArgumentException("unknown type: should not happen");
      }
    }

    StringBuilder getJvmSignature(TypeMirror type, boolean useFlatname) {
      StringBuilder sig = new StringBuilder();
      var s = primitiveSigOrNull(type);
      if (s != null) {
        sig.append(s);
        return sig;
      }

      TypeMirror t = types.erasure(type);
      JvmTypeVisitor jv = new JvmTypeVisitor(types);
      jv.visit(t, sig);

      return sig;
    }
  }

  static String primitiveSigOrNull(TypeMirror type) {
    return switch (type.getKind()) {
      case VOID -> SIG_VOID;
      case BOOLEAN -> SIG_BOOLEAN;
      case BYTE -> SIG_BYTE;
      case CHAR -> SIG_CHAR;
      case SHORT -> SIG_SHORT;
      case INT -> SIG_INT;
      case LONG -> SIG_LONG;
      case FLOAT -> SIG_FLOAT;
      case DOUBLE -> SIG_DOUBLE;
      default -> null;
    };
  }


  static void err(String msg) {
    throw new RuntimeException(msg);
  }
}
