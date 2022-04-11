# wildfly-custom-logger
Wildfly generating logger files dynamically using custom handler and Mapped Diagnostic Context (MDC) 

download the custom-logger.jar file and create Wildfly custom module in Wildfly server 

#Custom module in wildfly

Go to <Wildfly Home Dir>/modules/system/layers/base/org/jboss 
create a directory called custom-logger and create main folder inside custom-logger
Place custom-logger.jar inside main folder.
Create module.xml with following content 
```
<?xml version="1.0" encoding="UTF-8"?>

<module name="org.jboss.customlogmanager" xmlns="urn:jboss:module:1.8">
    <resources>
        <resource-root path="custom-logger.jar"/>
    </resources>

    <dependencies>
        <!-- for java.beans -->
        <module name="java.desktop"/>
        <module name="java.logging"/>
        <module name="java.xml"/>
        <module name="javax.json.api"/>
        <module name="org.jboss.modules"/>
        <module name="org.wildfly.common"/>
		<module name="org.jboss.logmanager"/>
		
    </dependencies>
</module> 
```
Open standalone.xml file and configure custom handler class using <custom-handler/> in loggers section 
```
<custom-handler name="customLogger" class="org.jboss.logmanager.handlers.CustomSizeRotatingFileHandler" module="org.jboss.custom-logger">
   <level name="INFO" />
   <formatter>
      <pattern-formatter pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] %M (%t) %s%e%n" />
   </formatter>
   <properties>
      <property name="fileName" value="${jboss.server.log.dir}/custom.log" />
      <property name="maxBackupIndex" value="1" />
      <property name="rotateOnBoot" value="false" />
      <property name="append" value="true" />
      <property name="autoFlush" value="true" />
      <property name="enabled" value="true" />
      <property name="rotateSize" value="20971520" />
      <property name="suffixFileName" value="_custom.log" />
      <property name="mdcKey" value="key" />
      <property name="basePath" value="${jboss.server.log.dir}" />
   </properties>
</custom-handler>
```
Configure custom handler to logger categroy 

add the following code to MDC key to add 

   org.jboss.logmanager.MDC.put("key","filename");
   
 add the following code in finaly block to remove the MDC key
 org.jboss.logmanager.MDC.remove("key");

