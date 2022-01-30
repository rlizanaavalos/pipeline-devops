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
          choice choices: ['gradle', 'maven'], name: 'buildTool'
      }
      stages {
          stage('Pipeline') {
              steps {
                  script {
		      sh 'printenv'
                      print("Pipeline")
                      print params.buildTool
//		      print STAGE
			  
		      def ci_or_cd = verifyBranchName()
			  
                      if (params.buildTool == 'gradle') {
                          println 'ejeutar gradle'
                          gradle()
                      } else {
                          println 'ejecutar maven'
                          maven()
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
              slackSend color: 'danger', channel: 'U02MU77P45S', message: "Build Failed: Rodrigo Lizana ${env.JOB_NAME} ${params.buildTool} Ejecución fallida en stage ${env.STAGE}", tokenCredentialId: 'slack-token-devops'
        }
      }
  }
}

def verifyBranchName () {
     return (env.GIT_BRANCH.contains('feature') || env.GIT_BRANCH.contains('develop')) ? 'CI' : 'CD'
}

return this;
