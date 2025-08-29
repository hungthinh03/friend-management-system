pipeline {
    agent any

    stages {
        stage('Clean Workspace') {
            steps {
                deleteDir()  // wipes the entire workspace
            }
        }

        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Prepare SQL Scripts') {
            steps {
                sh '''
                if [ ! -f ./sql/frienddb.sql ]; then
                    echo "ERROR: SQL file not found!"
                    exit 1
                fi
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker-compose -f compose.yml build'
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
