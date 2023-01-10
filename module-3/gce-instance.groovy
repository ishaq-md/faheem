pipeline {
    agent any

    environment {
    SVC_ACCOUNT_KEY = credentials('terraform-auth')
  }
     
    stages {
      	stage('Set creds') {
            steps {
              
                sh 'echo $SVC_ACCOUNT_KEY | base64 -d > ./Jenkins/jenkins.json'
		            sh 'pwd'
                       
               
            }
        }

	
	stage('Auth-Project') {
	 steps {
		 dir('Jenkins')
		 {
    
        sh 'gcloud auth activate-service-account jenkins@mi-dev-lab.iam.gserviceaccount.com --key-file=jenkins.json'
    }
    }
	}
 	 
	stage('Create Instance') {
	 steps {
    
    sh 'gcloud compute instances create $VM_NAME --zone=$ZONES --tags=http-server --metadata-from-file=startup-script=./scripts/startup-script-petclinic.sh'
        
    }
    }
    
    	stage('Collect External IP') {
	 steps {
    
         sh "gcloud compute instances describe $VM_NAME --zone=$ZONES --format='get(networkInterfaces[0].accessConfigs[0].natIP)' > ip.txt"
	     sh 'cat ip.txt'
        
    }
    }
    
    
  
    stage('App health check') {
	 steps {
            sh 'sleep 300'
	    sh 'curl http://$(cat ip.txt)'
    
    }
    }
   }
}
