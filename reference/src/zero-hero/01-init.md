# Initializing your developer account

```shell
java -jar adama.jar init
```

<font color="red">Please note the notice about early access!</font>

This will then prompt you with a blurb of text that outlines that providing your email will be implicit acceptance to Adama's terms, conditions, and privacy policy. You should read them!

Once we have your email, we will send you a verification email with a code. Please copy and paste that code into the terminal, and your account will be setup.

Oh, and if you want to revoke other machines, then this is a great time to do it by inputting Y when asked to revoke. This ability to revoke is a security feature if you lose a laptop or work from an insecure machine and want to secure your account.

This tool will drop a file (.adama) within your home directory to act as your default config. You can, of course, override this with the ```--config``` parameter.

[For now, let's move on towards kick starting a web application...](02-kickstart.md)