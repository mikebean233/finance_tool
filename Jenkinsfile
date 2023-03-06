pipeline {
  agent {
    kubernetes {
      yaml '''
        apiVersion: v1
        kind: Pod
        namespace: devops-tools
        metadata:
          labels:
            agent: some-label-value
        spec:
          serviceAccountName: jenkins-agent
          containers:
          - name: gradle
            image: gradle:jdk17
            command:
            - cat
            tty: true
            volumeMounts:
              - name: jenkins-agent-data
                mountPath: /var/gradle_home
            env:
              - name: GRADLE_OPTS
                value: -Xmx1024m
          - name: docker
            image: docker:cli
            command:
            - cat
            tty: true
            volumeMounts:
               - name: docker-socket
                 mountPath: /var/run/docker.sock
          - name: k8s-tools
            image: alpine/k8s:1.23.17
            command:
            - cat
            tty: true
          - name: npm
            image: node:lts-alpine3.17
            command:
            - cat
            tty: true
          volumes:
            - name: jenkins-agent-data
              persistentVolumeClaim:
                claimName: jenkins-agent-pvc
            - name: docker-socket
              hostPath:
                path:  /var/run/docker.sock
        '''
    }
  }
  options { checkoutToSubdirectory('checkout') }
  stages {
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
                  docker.build 'finance-tool:0.0.2-SNAPSHOT'
               }
            }
        }
      }
    }
    stage('npm build (web)') {
      steps {
        container('npm') {
          dir('checkout/finance-tool-web') {
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
              docker.build 'finance-tool-web:0.0.2-SNAPSHOT'
            }
          }
        }
      }
    }
    stage('docker image build (grafana)') {
      steps {
        container('docker') {
            dir("checkout/grafana") {
              script {
                  docker.build 'finance-tool-grafana:0.0.2-SNAPSHOT'
               }
            }
        }
      }
    }
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