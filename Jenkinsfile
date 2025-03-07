pipeline {
    agent any

    environment {
        JAVA_CONTAINER_NAME = 'on-hz-server'
        DOCKER_IMAGE = 'openjdk:17-jdk-slim'
        JAR_FILE = 'build/libs/app.jar'
        REMOTE_USER = 'onhz'
        REMOTE_SERVER = '220.116.96.179'
        REMOTE_PORT = '4342'
        REMOTE_PATH = '/Users/onhz/workspace/server_docker'
        REMOTE_JAR_NAME = 'app.jar'
    }

    stages {
        stage('#1 Checkout') {
            steps {
                git credentialsId: 'backend_credential', branch: 'main', url: 'https://github.com/On-Hz/server.git'
            }
        }

        stage('#2 Gradle Build & Test') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build test'
            }
        }

        stage('#3 Stop Container') {
            steps {
                sshagent(credentials: ['onhz-macmini']) {
                    sh """
                        ssh -p 4342 -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_SERVER} 'exec /bin/zsh -l -c "cd ${REMOTE_PATH}/deploy && ./manage_container.sh stop"'
                    """
                }
            }
        }

        stage('#4 Sleep') {
            steps {
                echo 'Waiting for 10 seconds...'
                script {
                    sleep time: 10, unit: 'SECONDS'
                }
            }
        }


        stage('#5 Backup Jar') {
            steps {
                sshagent(credentials: ['onhz-macmini']) {
                    sh "ssh -p ${REMOTE_PORT} -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_SERVER} 'cd ${REMOTE_PATH}/deploy && ./backup_jar.sh'"
                }
            }
        }

        stage('#6 Transfer Jar') {
            steps {
                sshagent(credentials: ['onhz-macmini']) {
                    sh "scp -P ${REMOTE_PORT} -o StrictHostKeyChecking=no ${JAR_FILE} ${REMOTE_USER}@${REMOTE_SERVER}:${REMOTE_PATH}/${REMOTE_JAR_NAME}"
                }
            }
        }

        stage('#7 Start Container') {
            steps {
                script {
                    sshagent(credentials: ['onhz-macmini']) {
                        try {
                            sh """
                                ssh -p 4342 -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_SERVER} 'exec /bin/zsh -l -c "cd ${REMOTE_PATH}/deploy && ./manage_container.sh start"'
                            """
                        } catch(e) {
                            echo 'Rollback Jar'
                            sh "ssh -p ${REMOTE_PORT} -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_SERVER} 'cd ${REMOTE_PATH}/deploy && ./rollback_jar.sh'"
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo '배포 완료'
        }
        failure {
            echo '배포 실패'
        }
    }
}
