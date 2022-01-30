def call(String[] stagesInput){
    def ejecutarStageCompile = false
    def ejecutarStageSonar = false
    def ejecutarStageRun = false
    def ejecutarStageTestapp = false
    def ejecutarStageUpload = false
    
    if (stagesInput == null) {
        ejecutarStageCompile = true
        ejecutarStageSonar = true
        ejecutarStageRun = true
        ejecutarStageTestapp = true
        ejecutarStageUpload = true
    } else {
        for (String stageInput: stagesInput) {
            switch(stageInput) {
                case "compile":
                    ejecutarStageCompile = true
                    break
                case "sonar":
                    ejecutarStageSonar = true
                    break
                case "run":
                    ejecutarStageCompile = true
                    ejecutarStageRun = true
                    break
                case "testapp":
                    ejecutarStageCompile = true
                    ejecutarStageRun = true
                    ejecutarStageTestapp = true
                    break
                case "upload":
                    ejecutarStageCompile = true
                    ejecutarStageUpload = true
                    break
                default:
                    error("No existe stage {$stageInput}")
                    break
            }
        }
    }
    
    if (ejecutarStageCompile) {
        stage('compile') {
            STAGE=env.STAGE_NAME
            sh 'chmod a+x gradlew'
            sh './gradlew build'
        }   
    }
    if (ejecutarStageSonar) {
        stage('sonar') {
            STAGE=env.STAGE_NAME
            scannerHome = tool 'sonar-scanner'
            withSonarQubeEnv('Sonarqube-server') {
                sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle2 -Dsonar.java.binaries=build"
            }
        }
    }
    if (ejecutarStageRun) {
        stage('run') {
            STAGE=env.STAGE_NAME
            sh 'nohup bash gradlew bootRun &'
            sleep(30)
        }
    }
    if (ejecutarStageTestapp) {
        stage('testapp') {
            STAGE=env.STAGE_NAME
            sh """curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"""
        }   
    }
    if (ejecutarStageUpload) {
        stage('upload') {
            STAGE=env.STAGE_NAME
            nexusPublisher nexusInstanceId: 'nexus3', nexusRepositoryId: 'test-gradle', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: '', filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'ejemplo-maven-feature-sonar', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
        }
    }
}

return this;
