package com.dhcc.framework.test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cxf.tools.java2ws.JavaToWS;

import com.dhcc.framework.annotation.Descript;

/**
 * webservice包装类及其wsdl生成器
 * 
 * @author 黎乐乔[joe7bit@163.com]
 */
public class WebServiceCodeGenerator extends AnnoParameterParser {

	/**
	 * 根据解析好的方法参数拼装ws包装类
	 */
	public void generateWebServiceCode() {
		WebServiceCodeGenerator.parseParams(preParseMap);
		System.err.println(parsedParamsMap.entrySet());
		if (parsedParamsMap.entrySet().size() > 0) {
			Iterator<Entry<String, Map<String, List<Map<String, String>>>>> entries = parsedParamsMap
					.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<String, Map<String, List<Map<String, String>>>> entry = entries
						.next();
				StringBuffer code = new StringBuffer();
				StringBuffer codeStringBuffer = new StringBuffer();
				String packageName = entry.getKey();
				String outputPackage = packageName.substring(0,
						packageName.lastIndexOf('.'));
				System.out.println("outputPackage====="+outputPackage);
				String[] outString = outputPackage.split("\\.");
				String outdir = "";
				for(int i = outString.length-1;i >=0;i-- ){
					if(i != 0){
						outdir += outString[i]+".";
						continue;
					}
				     outdir += outString[i];
				}
			
				
				String blhPackName = preParseMap.get(packageName)[0].getName();
				Class<?> blhClass = null;
				try {
					blhClass = Class.forName(blhPackName);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				Descript blh2WSClassAnno = blhClass.getAnnotation(Descript.class);
				Method[] methodObjs = blhClass.getMethods();
				Map<String, Method> methodMap = new HashMap<String, Method>();
				for (Method methodObj : methodObjs) {
					methodMap.put(methodObj.getName(), methodObj);
				}
				/** 生成webservice接口类 */
				String dtoPackName = preParseMap.get(packageName)[2].getName();
				String blhClassName = blhPackName.substring(blhPackName.lastIndexOf('.') + 1);
				String dtoClassName = dtoPackName.substring(dtoPackName.lastIndexOf('.') + 1);
				String entityName = dtoClassName.substring(0,dtoClassName.indexOf("Dto"));
				String outFileName = entityName + "WServiceInterface";
				String outFileNames = entityName + "WService";
				code.append("package " + outputPackage + ";\n\n");
				code.append("import javax.jws.WebMethod;\n");
				code.append("import javax.jws.WebService;\n");
				code.append("import javax.jws.soap.SOAPBinding;\n\n\n");
				code.append("@WebService\n");
				/*(wsdlLocation=\"http://localhost:8080/"
						+System.getProperty("user.dir").split("\\\\")[System.getProperty("user.dir").split("\\\\").length-1]
					    +"/ws/" + lowerFirstChar(entityName) + "WService\""
						+ ")\n*/
				code.append("@SOAPBinding(style=SOAPBinding.Style.RPC)\n");
				code.append("public interface " + outFileName + " {\n\n");

				/**生成接口实现类 */
				codeStringBuffer.append("package " + outputPackage + ";\n\n");
				codeStringBuffer.append("import javax.annotation.Resource;\n");
				codeStringBuffer.append("import javax.annotation.PostConstruct;\n");
				codeStringBuffer.append("import org.apache.commons.logging.Log;\n");
				codeStringBuffer.append("import org.apache.commons.logging.LogFactory;\n");
				codeStringBuffer.append("import org.springframework.stereotype.Component;\n");
				codeStringBuffer.append("import javax.jws.WebMethod;\n");
				codeStringBuffer.append("import javax.jws.WebService;\n");
				codeStringBuffer.append("import " + blhPackName + ";\n");
				codeStringBuffer.append("import " + dtoPackName + ";\n");
				codeStringBuffer.append("import com.dhcc.framework.transmission.event.BusinessRequest;\n\n");
				codeStringBuffer.append("/**\n");
				codeStringBuffer.append(" * @author auto-generated by AnnoParameterParser\n");
				codeStringBuffer.append(" * @timestamp "+ (new java.sql.Timestamp(System.currentTimeMillis()).toString()) + "\n");
				codeStringBuffer.append(" */\n");
				if (blh2WSClassAnno != null) {
					codeStringBuffer.append("@com.dhcc.framework.annotation.Descript(\""+ blh2WSClassAnno.value() + "\")\n");
				}
				codeStringBuffer.append("@WebService(endpointInterface = "+"\""+""+outputPackage+"."+outFileName+""+"\""+"");
				codeStringBuffer.append(",targetNamespace="+"\""+"http://"+outdir+""+"\""+",\n");
				codeStringBuffer.append("portName="+"\""+""+outFileNames+"ServiceHttpSoap12Endpoint"+"\""+")\n");
				codeStringBuffer.append("public class " + outFileNames+ " implements " + outFileName + "{\n\n");
				codeStringBuffer.append("    private static Log logger = LogFactory.getLog("+ outFileName + ".class);\n\n");
				codeStringBuffer.append("    @Resource\n");
				codeStringBuffer.append("    private " + blhClassName+ " blh;\n\n");

				Iterator<Map.Entry<String, List<Map<String, String>>>> methods = entry.getValue().entrySet().iterator();
				while (methods.hasNext()) {
					Entry<String, List<Map<String, String>>> method = methods.next();
					Iterator<Entry<String, String>> params = method.getValue().get(0).entrySet().iterator();
					String methodParameters = "";

					Method methodObj = methodMap.get(method.getKey());
					Descript blh2WSMethodAnno = methodObj
							.getAnnotation(Descript.class);
					if (blh2WSMethodAnno != null) {
						codeStringBuffer.append("    @com.dhcc.framework.annotation.Descript(\""+ blh2WSMethodAnno.value() + "\")\n");
					}
					String returnType = method.getValue().get(1).get("RETURN_TYPE");
					System.out.println("=========method======"+method);
					String descript = method.getValue().get(4).get("descript");
					String testvalues = method.getValue().get(3).get("testvalues");
					String[] testvalueStrings = testvalues.split(";");
					StringBuffer core = new StringBuffer(); // 循环拼接核心代码
					String[] paramNameArray = null;
					String[] paramTypeArray = null;
					Map<String, Object> cache = new HashMap<String, Object>(); // 回溯缓存标记

					while (params.hasNext()) {
						Entry<String, String> param = params.next();
						String paramNameChain = param.getKey();
						String paramTypeChain = param.getValue();
						methodParameters += paramTypeChain.substring(paramTypeChain.lastIndexOf('|') + 1)
								+ " "
								+ paramNameChain.substring(paramNameChain.lastIndexOf('.') + 1) + ", ";

						paramNameArray = paramNameChain.split("\\.");
						paramTypeArray = paramTypeChain.split("\\|");
						int len = paramNameArray.length;
						for (int i = 0; i < len - 1; i++) { // 参数方法链路处理
							if (cache.get(paramNameArray[i]) == null) {
								core.append("        " + paramTypeArray[i]
										+ " " + paramNameArray[i] + " = new "
										+ correctType(paramTypeArray[i])
										+ "();\n");
								if (paramTypeArray[i]
										.startsWith("java.util.Map")) {
									core.append("        "
											+ paramedType(paramTypeArray[i])[0]
											+ " "
											+ paramedName(paramedType(paramTypeArray[i])[0])
											+ " = new "
											+ correctType(paramedType(paramTypeArray[i])[0])
											+ "();\n");
									core.append("        "
											+ paramedType(paramTypeArray[i])[1]
											+ " "
											+ paramedName(paramedType(paramTypeArray[i])[1])
											+ " = new "
											+ correctType(paramedType(paramTypeArray[i])[1])
											+ "();\n");
									if (cache.get(paramNameArray[i + 1]) == null
											&& i < len - 2) {
										core.append("        "
												+ paramTypeArray[i + 1]
												+ " "
												+ paramNameArray[i + 1]
												+ " = new "
												+ correctType(paramTypeArray[i + 1])
												+ "();\n");
										cache.put(paramNameArray[i + 1],
												"OBJECT");
									}
									core.append("        "
											+ paramedName(paramedType(paramTypeArray[i])[1])
											+ ".set"
											+ upperFirstChar(paramNameArray[i + 1])
											+ "(" + paramNameArray[i + 1]
											+ ");\n");
									core.append("        "
											+ paramNameArray[i]
											+ ".put"
											+ "("
											+ paramedName(paramedType(paramTypeArray[i])[0]
													+ ","
													+ paramedName(paramedType(paramTypeArray[i])[1]
															+ ");\n")));
									cache.put(paramNameArray[i + 1], "METHOD");
								}
								if (paramTypeArray[i]
										.startsWith("java.util.Set")
										|| paramTypeArray[i]
												.startsWith("java.util.List")) {
									core.append("        "
											+ paramedType(paramTypeArray[i])[0]
											+ " "
											+ paramedName(paramedType(paramTypeArray[i])[0])
											+ " = new "
											+ correctType(paramedType(paramTypeArray[i])[0])
											+ "();\n");
									if (cache.get(paramNameArray[i + 1]) == null
											&& i < len - 2) {
										core.append("        "
												+ paramTypeArray[i + 1]
												+ " "
												+ paramNameArray[i + 1]
												+ " = new "
												+ correctType(paramTypeArray[i + 1])
												+ "();\n");
										cache.put(paramNameArray[i + 1],
												"OBJECT");
									}
									core.append("        "
											+ paramedName(paramedType(paramTypeArray[i])[0])
											+ ".set"
											+ upperFirstChar(paramNameArray[i + 1])
											+ "(" + paramNameArray[i + 1]
											+ ");\n");
									core.append("        "
											+ paramNameArray[i]
											+ ".add"
											+ "("
											+ paramedName(paramedType(paramTypeArray[i])[0]
													+ ");\n"));
									cache.put(paramNameArray[i + 1], "METHOD");
								}
								cache.put(paramNameArray[i], "OBJECT");
							}
						}
						for (int j = 1; j < len; j++) {
							if (cache.get(paramNameArray[j]) == "METHOD") {
								continue;
							}
							core.append("        " + paramNameArray[j - 1]
									+ ".set"
									+ upperFirstChar(paramNameArray[j]) + "("
									+ paramNameArray[j] + ");\n");
						}

					}
					methodParameters = methodParameters.substring(0,
							methodParameters.lastIndexOf(','));
					
					code.append("   /**\n");
					code.append("    * 方法名:                "+method.getKey()+"\n");
					code.append("    * 方法功能描述：                                          "+descript+"\n");
				/*	if(testvalueStrings.length == 1){
						System.out.println(testvalueStrings[0]);
						System.out.println(testvalueStrings[0].split(":")[1]);
						code.append("    * @param  "+methodParameters.split(" ")[1]+"  "+testvalueStrings[0].split(":")[1]+"  类型是{@linkplain "+methodParameters.split(" ")[0]+"}\n");
					}else if(testvalueStrings.length > 1){*/
						String muString = "";
						for(int k = 0;k<testvalueStrings.length;k++){
							if(testvalueStrings[k].split(",").length > 1){
								code.append("    * @param  "+methodParameters.split(" ")[1]+"  "+testvalueStrings[k].split(":")[1]+"  类型是{@linkplain "+methodParameters.split(" ")[0]+"}\n");
							}else{
								if(k == testvalueStrings.length-1){
									muString += testvalueStrings[k].split("=")[0]+" "+testvalueStrings[k].split(":")[1];
									break;
								}
								muString += testvalueStrings[k].split("=")[0]+" "+testvalueStrings[k].split(":")[1]+"  ";
							}
						}
						if(!"".equals(muString)){
							code.append("    * @param  "+muString+"\n");
						}
					
				/*	}*/
			
					code.append("    * @return "+returnType+"   术语类型是{@linkplain "+methodParameters.split(" ")[0]+"}  值域类型是{@linkplain "+methodParameters.split(" ")[0]+"}\n");
					code.append("    * @see "+methodParameters.split(" ")[0]+"\n");
					code.append("    * @see "+methodParameters.split(" ")[0]+"\n");
					code.append("    * @since JDK1.7\n");
					code.append("    * @Create Date:        "
							+ (new java.sql.Timestamp(System.currentTimeMillis())
							.toString()) + "\n");
					code.append("    */\n");
					
					code.append("    @WebMethod\n");
					
					code.append("    public " + returnType + " "
							+ method.getKey() + "(" + methodParameters
							+ ") ;\n\n\n");
					
					codeStringBuffer.append("   /**\n");
					codeStringBuffer.append("    * 方法名:                "+method.getKey()+"\n");
					codeStringBuffer.append("    * 方法功能描述：                                          "+descript+"\n");
						String musString = "";
						for(int k = 0;k<testvalueStrings.length;k++){
							if(testvalueStrings[k].split(",").length > 1){
								codeStringBuffer.append("    * @param  "+methodParameters.split(" ")[1]+"  "+testvalueStrings[k].split(":")[1]+"  类型是{@linkplain "+methodParameters.split(" ")[0]+"}\n");
							}else{
								if(k == testvalueStrings.length-1){
									musString += testvalueStrings[k].split("=")[0]+" "+testvalueStrings[k].split(":")[1];
									break;
								}
								musString += testvalueStrings[k].split("=")[0]+" "+testvalueStrings[k].split(":")[1]+"  ";
							}
						}
						if(!"".equals(musString)){
							codeStringBuffer.append("    * @param  "+musString+"\n");
						}
					
				/*	}*/
			
					codeStringBuffer.append("    * @return "+returnType+"   术语类型是{@linkplain "+methodParameters.split(" ")[0]+"}  值域类型是{@linkplain "+methodParameters.split(" ")[0]+"}\n");
					codeStringBuffer.append("    * @see "+methodParameters.split(" ")[0]+"\n");
					codeStringBuffer.append("    * @see "+methodParameters.split(" ")[0]+"\n");
					codeStringBuffer.append("    * @since JDK1.7\n");
					codeStringBuffer.append("    * @Create Date:        "
							+ (new java.sql.Timestamp(System.currentTimeMillis())
							.toString()) + "\n");
					codeStringBuffer.append("    */\n");
					
					codeStringBuffer.append("    @Override\n");
					codeStringBuffer.append("    @WebMethod\n");
					codeStringBuffer.append("    public " + returnType + " "
							+ method.getKey() + "(" + methodParameters
							+ ") {\n\n");
					codeStringBuffer.append("        " + dtoClassName
							+ " dto = new " + dtoClassName + "();\n");
					codeStringBuffer.append(core.toString());
					System.out.println(entityName+"-=-=-=-=-");
					System.out.println(upperFirstChar(entityName)+"-=-=-=-=-");
					//codeStringBuffer.append("        dto.set" + entityName
					//		+ "(" + lowerFirstChar(entityName) + ");\n");
					codeStringBuffer
							.append("        BusinessRequest request = new BusinessRequest();\n");
					codeStringBuffer.append("        request.setDto(dto);\n\n");
					codeStringBuffer.append("        try {\n");
					codeStringBuffer.append("            blh."
							+ method.getKey() + "(request);\n");
					codeStringBuffer.append("        } catch(Exception e) {\n");
					codeStringBuffer
							.append("            logger.error(e.getMessage(), e);\n");
					codeStringBuffer.append("        }\n");
					if (returnType != "void") {
						codeStringBuffer
								.append("        if(dto.getPageModel() != null) {\n");
						if (returnType.endsWith(">")) {
							codeStringBuffer.append("            return ("
									+ returnType
									+ ")dto.getPageModel().getPageData();\n");
						} else {
							codeStringBuffer
									.append("            return ("
											+ returnType
											+ ")dto.getPageModel().getPageData().get(0);\n");
						}
						codeStringBuffer.append("        }\n");
						codeStringBuffer.append("        return null;\n");
					}
					codeStringBuffer.append("    }\n\n");
				}
				code.append("}");
				writeCodeFile("main", code.toString(), outputPackage,outFileName);
				logger.info("wrapper codes generated to " + outputPackage);

				codeStringBuffer.append("    @PostConstruct\n");
				codeStringBuffer.append("    private void preRegister() {\n");
				codeStringBuffer.append("        com.dhcc.framework.common.WsInfoHolder.registWsInfo("
								+ outFileName + ".class);\n");
				codeStringBuffer.append("    }\n");
				codeStringBuffer.append("}");
				writeCodeFile("main", codeStringBuffer.toString(),outputPackage, outFileNames);
				logger.info("wrapper codes generated to " + outputPackage);

				try {
					Thread.sleep(1); // 确保系统不会乱序
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				String compiledFile = "target/classes/"
						+ outputPackage.replace('.', '/') + "/" + outFileNames
						+ ".class";
				System.out.println(compiledFile);
				
				
				/**** 生成wsdl文件*******************************************************************************/
				if (new File(compiledFile).exists()) { // 若生成的源文件已编译好就自动再生成其wsdl文件
					System.out.println("==========================="+ compiledFile);
					try {
						Class<?> targetClass = Class.forName(outputPackage+ "." + outFileNames);
						String wsdlTargetDir = "webContent/wsdl";
						String soapAddress = "/ws/"+ lowerFirstChar(outFileNames);
						String frontEnd = "simple";
						String[] wsdlArgs = new String[] { "-wsdl", "-fe",
								frontEnd, "-a", soapAddress, "-d",
								wsdlTargetDir,"-o",outFileNames+".wsdl", targetClass.getName() };
						new JavaToWS(wsdlArgs).run();
						logger.info("wsdl for " + outFileNames+ " is generated to " + wsdlTargetDir);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					logger.error("there's no " + outFileNames
							+ " source codes exist or it's not compiled yet.");
				}

				/**
				 * 自动生成applicationWSServer.xml文件，add by jitao -20140402。
				 * */
				try {
					Thread.sleep(1); // 确保系统不会乱序
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (new File(compiledFile).exists()) { // 若生成的源文件已编译好就自动再生成其wsdl文件
					StringBuffer spCodeBuffer = new StringBuffer();
					String classNameBuffer = "";
					try {
						Class<?> targetClassa = Class.forName(outputPackage
								+ "." + outFileNames);
						classNameBuffer = targetClassa.getName();
					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					spCodeBuffer.append("<?xml version=" + "\"1.0\""
							+ " encoding=" + "\"UTF-8\"" + "?>     ");
					spCodeBuffer.append("\n");
					spCodeBuffer.append("   <beans xmlns="
							+ "\"http://www.springframework.org/schema/beans\""
							+ " xmlns:xsi="
							+ "\"http://www.w3.org/2001/XMLSchema-instance\""
							+ " xmlns:jaxws="
							+ "\"http://cxf.apache.org/jaxws\"");
					spCodeBuffer.append("\n");
					spCodeBuffer
							.append("   xsi:schemaLocation="
									+ "\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd ");
					spCodeBuffer.append("\n");
					spCodeBuffer
							.append("   http://cxf.apache.org/jaxws http://cxf.apache.org/schemas/jaxws.xsd\""
									+ ">");
					spCodeBuffer.append("\n");
					spCodeBuffer.append("<import resource="
							+ "\"classpath:META-INF/cxf/cxf.xml\"" + "/>");
					spCodeBuffer.append("\n");
					spCodeBuffer
							.append("<import resource="
									+ "\"classpath:META-INF/cxf/cxf-extension-soap.xml\""
									+ "/>");
					spCodeBuffer.append("\n");
					spCodeBuffer.append("<import resource="
							+ "\"classpath:META-INF/cxf/cxf-servlet.xml\""
							+ " />");
					spCodeBuffer.append("\n");
					spCodeBuffer.append("<bean id=\""
							+ lowerFirstChar(outFileNames) + "\" class=\""
							+ classNameBuffer + "\" />");
					spCodeBuffer.append("\n");
					spCodeBuffer.append("<jaxws:endpoint id=\""
							+ lowerFirstChar(outFileNames) + "WS" + "\""
							+ " address=\"/" + lowerFirstChar(outFileNames)+"\""
							+ " wsdlLocation=\"" + "/wsdl/"
							+ outFileNames +".wsdl"
							+ "\" implementor=\"#"
							+ lowerFirstChar(outFileNames) + "\">");
					spCodeBuffer.append("\n");
					spCodeBuffer.append("</jaxws:endpoint>");
					spCodeBuffer.append("\n");
					spCodeBuffer.append("</beans>");
					writeCodeFile("wsspringxml", spCodeBuffer.toString(), "",
							"application"+entityName+"WSServer");
					logger.info("====================applicationWSServer.xml for "
							+ outFileName + " is generated ");
					/** 生成webservice的spring配置文件end */
				} else {
					logger.error("there's no " + outFileNames
							+ " source codes exist or it's not compiled yet.");
				}
			}
		} else {
			logger.error("there's nothing to generate. please check the annotations in blh and action.");
		}
	}
}