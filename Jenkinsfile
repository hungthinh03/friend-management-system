pipeline {
    agent any
    stages {
        stage('Update Code') {
            steps {
                script {
                    // Ensure workspace is clean and up-to-date
                    sh 'git reset --hard'
                    sh 'git clean -fd'
                    sh 'git pull origin master'
                }
            }
        }

        stage('Redeploy with Docker Compose') {
            steps {
                script {
                    // Stop and remove old containers
                    sh 'docker-compose -f compose.yml down'
                    // Build images and start containers (force rebuild)
                    sh 'docker-compose -f compose.yml up -d --build --no-cache'
                }
            }
        }
    }
}
