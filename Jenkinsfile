pipeline {
    agent any

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Build Gradle') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker-compose -f compose.yml build'
            }
        }

        stage('Prepare SQL Scripts') {
            steps {
                // Just check that the SQL file exists; no moving or renaming needed
                sh '''
                if [ ! -f ./sql/frienddb.sql ]; then
                    echo "ERROR: SQL file not found!"
                    exit 1
                fi
                '''
            }
        }

        stage('Recreate DB') {
            steps {
                sh 'docker-compose -f compose.yml down -v'
            }
        }

        stage('Up Containers') {
            steps {
                sh 'docker-compose -f compose.yml up --abort-on-container-exit'
            }
        }

        stage('Health Check') {
            steps {
                sh 'docker ps'
            }
        }
    }

    post {
        always {
            sh 'docker ps'
        }
    }
}
