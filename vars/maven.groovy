def call(){
    stage('compile') {
        STAGE=env.STAGE_NAME
        sh './mvnw clean compile -e'
    }
    stage('sonar') {
        STAGE=env.STAGE_NAME
        scannerHome = tool 'sonar-scanner'
        withSonarQubeEnv('Sonarqube-server') {
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-maven -Dsonar.java.binaries=build"
        }
    }
    stage('test') {
        STAGE=env.STAGE_NAME
        sh './mvnw clean test -e'
    }
    stage('jar') {
        STAGE=env.STAGE_NAME
        sh './mvnw clean package -e'
    }
    stage('Run') {
        STAGE=env.STAGE_NAME
        sh 'nohup bash mvnw spring-boot:run &'
        sleep(30)
    }
    stage('TestApp') {
        STAGE=env.STAGE_NAME
        sh """curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"""
    }
    stage('uploadNexus') {
        STAGE=env.STAGE_NAME
        nexusPublisher nexusInstanceId: 'nexus3', nexusRepositoryId: 'test-gradle', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: 'build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'ejemplo-maven-feature-sonar', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
    }
}
return this;