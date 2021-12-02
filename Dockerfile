FROM ddrm86/wildfly:preview-25.0.1.Final
COPY target/numbot-1.0-SNAPSHOT.war /opt/jboss/wildfly/standalone/deployments/
