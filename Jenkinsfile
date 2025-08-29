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

        stage('Prepare SQL Folder') {
            steps {
                sh '''
                echo "==> Listing contents of ./sql folder"
                ls -l ./sql
                '''
            }
        }

        stage('Verify SQL file') {
            steps {
                sh 'ls -l ./sql'
                sh 'head -n 5 ./sql/frienddb.sql'
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
