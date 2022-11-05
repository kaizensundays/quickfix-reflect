set JAVA_HOME=%JAVA_8_HOME%

set MAVEN_OPTS="-Xmx1g"

mvn clean install

@rem mvn clean install -P test
