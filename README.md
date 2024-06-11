## Installation
> You need to have docker installed *(see https://docs.docker.com/engine/install/)*
1. Set up Google Credentials
	1) Configure your Google OAuth2.0 Consent Screen in Google Cloud Console *(see https://developers.google.com/workspace/marketplace/configure-oauth-consent-screen)*
	2) Create credentials:
		1. Click on `CREATE CREDENTIALS` in the **Credentials** tab and select `OAuth client ID` in the drop-down menu.
		2. Select `Web application` as **Application type**
		3. Enter the name of the OAuth2.0 Client
		4. In the **Authorized redirect URIs** section click on **ADD URI** and enter `http://localhost:8080/login/oauth2/code/google`
		5. Click on **Create** and save the `Client ID` and `Client Secret` *(you can also download them as a json file)*
2. Create `.env` file in the `blog-post-backend/docker/` directory and fill it with the environment variables used in the `docker-compose.yml`. The format is `name=value` with every variable being on a separate line.
	- `OAUTH2_CLIENT_ID` - the client ID retrieved while creating OAuth2 credentials.
	- `OAUTH2_CLIENT_SECRET` - the client secret retrieved while creating OAuth2 credentials.
3. *(Optional)* Set up credentials for Amazon S3 *(see https://docs.aws.amazon.com/solutions/latest/data-transfer-hub/set-up-credentials-for-amazon-s3.html)*
4. *(Optional)* Set up credentials for email service. 
The example is for **Gmail**, but you can use your prefered e-mail provider.
	1) Go to **Manage your Google account** and click on **Security** on the sidebar.
	2) Click on **2-step verification** *(and enable it if it's not enabled already)*
	3) Click on App passwords (or visit https://myaccount.google.com/apppasswords directly)
	4) Enter the app name *BlogPost* or any other custom one
	5) Copy the password
	6) Set environment variables in `.env` file
		- `EMAIL_USERNAME` - your email address.
		- `EMAIL_PASSWORD` - copied password.
		- `EMAIL_HOST` - your e-mail providerss SMPT-address **(if you use Gmail you can skip this variable)**
5. *(Optional)* Set other variable to you liking, if they aren't set - default one's are being used:
	- `POSTGRES_USER`
	- `POSTGRES_PASSWORD`
	- `REDIS_PASSWORD`
	- `MINIO_USERNAME`
	- `MINIO_PASSWORD`
	- `MINIO_ACCESS_KEY`
	- `MINIO_SECRET_KEY`
	- `JWT_SECRET_KEY`
6. Run `docker-compose -f blog-post-backend/docker/docker-compose.yml up` in the terminal to start the backend/docker/docker-compose
7. In separate terminal start the frontend by running `npm start --prefix blog-post-frontend` in the terminal