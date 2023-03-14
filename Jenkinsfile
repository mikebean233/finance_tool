def apiBuildPublish() {
    stage('gradle build (api)') {
      steps {
        container('gradle') {
          dir('checkout') {
            sh './gradlew build -i --build-cache --no-daemon --gradle-user-home=/var/gradle_home'
            sh 'ls ./build/libs -All'
            sh 'rm ./build/libs/*plain.jar'
          }
        }
      }
    }
    stage('docker image build (api)') {
      steps {
        container('docker') {
          dir("checkout") {
            script {
              docker.build 'finance-tool:0.0.5-SNAPSHOT'
            }
          }
        }
      }
    }
}

def webBuildPublish() {
    stage('npm build (web)') {
      steps {
        container('npm') {
          dir('checkout/finance-tool-web') {
            sh 'npm install'
            sh 'npm install react-scripts@5.0.1'
            sh 'npm run build'
          }
        }
      }
    }
    stage('docker image build (web)') {
      steps {
        container('docker') {
          dir('checkout/finance-tool-web') {
            script {
              docker.build 'finance-tool-web:0.0.5-SNAPSHOT'
            }
          }
        }
      }
    }
}

def dockerPublish() {
    stage('docker image build (grafana)') {
      steps {
        container('docker') {
          dir("checkout/grafana") {
            script {
              docker.build 'finance-tool-grafana:0.0.5-SNAPSHOT'
            }
          }
        }
      }
    }
}

pipeline {
  agent {
    kubernetes {
      yamlFile 'agent-pod.yaml'
    }
  }
  options { checkoutToSubdirectory('checkout') }
  stages {
	apiBuildPublish()
	webBuildPublish()
	dockerPublish()

    stage('deploy') {
      steps {
        container('k8s-tools') {
          dir("checkout/CICD/dev") {
            sh 'kubectl kustomize ./ > combined.yaml'
            sh 'kubectl apply -f combined.yaml'
          }
        }
      }
    }
  }
}