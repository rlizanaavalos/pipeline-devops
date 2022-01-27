def call(){
    stage('Compile') {
        STAGE=env.STAGE_NAME
        sh 'chmod a+x gradlew'
        sh './gradlew build'
    }
    stage('SonarQube analysis') {
        STAGE=env.STAGE_NAME
        scannerHome = tool 'sonar-scanner'
        withSonarQubeEnv('Sonarqube-server') {
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle2 -Dsonar.java.binaries=build"
        }
    }
    stage('Run') {
        STAGE=env.STAGE_NAME
        sh 'nohup bash gradlew bootRun &'
        sleep(30)
    }
    stage('TestApp') {
        STAGE=env.STAGE_NAME
        sh """curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"""
    }
    stage('uploadNexus') {
        STAGE=env.STAGE_NAME
        nexusPublisher nexusInstanceId: 'nexus3', nexusRepositoryId: 'test-gradle', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'ejemplo-maven-feature-sonar', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
    }
}

return this;
