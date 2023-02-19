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
                  docker.build 'finance-tool:dev'
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
                  docker.build 'finance-tool-grafana:dev'
               }
            }
        }
      }
    }
  }
}