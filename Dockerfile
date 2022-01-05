FROM ddrm86/wildfly:preview-25.0.1.Final

COPY hsqldb.jar $JBOSS_HOME/modules/system/layers/base/org/hsqldb/main/
COPY module.xml $JBOSS_HOME/modules/system/layers/base/org/hsqldb/main/
COPY standalone.xml $JBOSS_HOME/standalone/configuration/

COPY target/numbot-1.0.war $JBOSS_HOME/standalone/deployments/
