/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
    pipeline {
        agent any
        
        environment {
            STAGE = ''
        }

        parameters {
            string 'stage'
            choice choices: ['gradle', 'maven'], name: 'buildTool'
        }
        stages {
            stage('Pipeline') {
                steps {
                    script {
                        print("Pipeline")
                        print params.buildTool
                        print formatInputStage(params.stage)
                        if (params.buildTool == 'gradle') {
                            println 'ejeutar gradle'
                            gradle(formatInputStage(params.stage))
                        } else {
                            println 'ejecutar maven'
                            maven(formatInputStage(params.stage))
                        }
                    }
                }
            }
        }
        post {
            // only triggered when blue or green sign
            success {
                    slackSend color: 'good', channel: 'U02MU77P45S', message: "Build Success: Rodrigo Lizana ${env.JOB_NAME} ${params.buildTool} Ejecución exitosa", tokenCredentialId: 'slack-token-devops'
            }
            // triggered when red sign
            failure {
                    slackSend color: 'danger', channel: 'U02MU77P45S', message: "Build Failed: Rodrigo Lizana ${env.JOB_NAME} ${params.buildTool} Ejecución fallida en stage ${STAGE}", tokenCredentialId: 'slack-token-devops'
            }
        }
    }
}

def formatInputStage (String stages) {
    if (stages.length() > 0) {
        return stages.split(';');
    }
    return null
}

return this;
