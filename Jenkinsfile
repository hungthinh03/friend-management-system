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

        stage('Debug SQL Scripts') {
            steps {
                echo 'Listing SQL scripts inside the DB container mount:'
                sh '''
                docker run --rm -v $PWD/sql:/docker-entrypoint-initdb.d alpine ls -l /docker-entrypoint-initdb.d
                '''
            }
        }

        stage('Prepare SQL Scripts') {
            steps {
                sh '''
                rm -rf ./sql/frienddb.sql
                mv ./sql/frienddb/*.sql ./sql/frienddb.sql
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
                echo 'Health check skipped (depends on previous stages)'
            }
        }
    }

    post {
        always {
            sh 'docker ps'
            echo 'Build finished.'
        }
    }
}
