properties properties: [
  [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '30', numToKeepStr: '10']],
  disableConcurrentBuilds()
]

@Library('mare-build-library')
def git = new de.mare.ci.jenkins.Git()

timeout(60) {
  node {
    def buildNumber = env.BUILD_NUMBER
    def branchName = env.BRANCH_NAME
    def workspace = env.WORKSPACE
    def buildUrl = env.BUILD_URL

    try {
      withEnv(["JAVA_HOME=${tool 'JDK8'}", "PATH+MAVEN=${tool 'Maven3'}/bin:${env.JAVA_HOME}/bin"]) {

        // PRINT ENVIRONMENT TO JOB
        echo "workspace directory is $workspace"
        echo "build URL is $buildUrl"
        echo "build Number is $buildNumber"
        echo "branch name is $branchName"
        echo "PATH is $env.PATH"

        stage('Checkout') {
          checkout scm
        }
        
        dir('zap-maven-plugin-parent') {
          stage('Build') {
            sh "mvn package -DskipUnitTests=true"
          }

          stage('Unit-Tests') {
            sh "mvn test -Dmaven.test.failure.ignore"
          }

          stage('Integration-Tests') {
            // TODO add for OWASP ZAP version 2.7.0
          }

          stage('Deploy') {
            if(git. isProductionBranch()){
              sh "mvn -Prelease package source:jar gpg:sign install:install deploy:deploy"
            } else {
              sh "mvn deploy"
            }
          }
        }

        archiveArtifacts artifacts: '*/target/*.jar'
        junit healthScaleFactor: 1.0, testResults: '*/target/surefire-reports/TEST*.xml'
      }
    } catch (e) {
      mail subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}): Error on build", to: 'github@martinreinhardt-online.de', body: "Please go to ${env.BUILD_URL}."
      throw e
    }
  }
}
