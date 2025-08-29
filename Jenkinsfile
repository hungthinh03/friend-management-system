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
                sh '''
                # Ensure frienddb.sql is a file, not a folder
                if [ -d ./sql/frienddb.sql ]; then
                    rm -rf ./sql/frienddb.sql
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
                sh 'docker-compose -f compose.yml up -d'
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
