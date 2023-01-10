pipeline {
    agent any

    environment {
    SVC_ACCOUNT_KEY = credentials('terraform-auth')
  }
     
    stages {
      	stage('Set creds') {
            steps {
              
                sh 'echo $SVC_ACCOUNT_KEY | base64 -d > ./module-3/jenkins.json'
		            sh 'pwd' 
               
            }
        }

	
	stage('Auth-Project') {
	 steps {
		 dir('module-3')
		 {
    
        sh 'gcloud auth activate-service-account jenkins@mi-dev-lab.iam.gserviceaccount.com --key-file=jenkins.json'
    }
    }
	}
 	 
	stage('Create Instance') {
	 steps {
    
    sh 'gcloud compute instances create $VM_NAME --zone=$ZONES --tags=http-server'
        
    }
    }
     
   }
}
