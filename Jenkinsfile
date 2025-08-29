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
                sh 'docker-compose -f compose.yml up -d'
            }
        }

        stage('Run SQL Script') {
            steps {
                sh '''
                echo "==> Waiting for DB to be healthy..."
                until [ "$(docker inspect -f '{{.State.Health.Status}}' frienddb)" == "healthy" ]; do
                    sleep 2
                done

                echo "==> Executing SQL script inside DB container"
                docker exec -i frienddb psql -U postgres -d FriendDB < ./sql/frienddb.sql
                '''
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
