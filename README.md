# CEmail
Mail receiving and sending library based on JavaMail library package

## Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
## Add the dependency
```
dependencies {
	implementation 'com.github.ccg520plus:CEmail:1.0.1'
}    
```    
## use:
### 1.Verify email account:

``` 
EmailConfig emailConfig = new EmailConfig()
            .setUsername(username)
            .setPassword(password)
            .setDebug(true);
EmailClient mClient = new EmailClient()
	.setEmailConfig(emailConfig)
	.imap("imap.gmail.com",993);
mClient.loginAuth();
```

Since this step is a time-consuming operation, it must be executed in a child thread.

### 2.read email:
```
int start = 0;
int size = 10;
mClient.readingAsync(start, size, "INBOX",new OnEmailResultListener() {
                    @Override
                    public void onReceiveResult(List<Email> emails) {
                        
                    }

                    @Override
                    public void onSendResult() {

                    }

                    @Override
                    public void onGetFolders(List<String> folderNames) {

                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });
```

### 3.send email,but the host and port in the mailbox configuration need to be updated:

```
mClient.smtp("smtp.gmail.com",587);
mClient.sendAsync("SubTitle", "content", "xxxx@hotmail.com", new OnEmailResultListener() {
                    @Override
                    public void onReceiveResult(List<Email> emails) {
                        
                    }

                    @Override
                    public void onSendResult() {

                    }

                    @Override
                    public void onGetFolders(List<String> folderNames) {

                    }

                    @Override
                    public void onFailed(Exception e) {

                    }
                });
```




    
