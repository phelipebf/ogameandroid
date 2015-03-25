# Introduction #

Since OGame is available in many languages it would be great to archive the same thing for this app. The following text explains how you could help to translate the app into another language.

# Steps #

### 1) Check if your language already exists ###

You need to know the 639-1 Code of your language. _(Check [this](http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes) page for a list)_

When you got the code go to http://code.google.com/p/ogameandroid/source/browse/trunk/res/ and look for a folder called **values-??** _(?? being the code)_

If the folder exists it will contain a file called strings.xml. You can use this file as a base to update or edit the translation.

### 2) Get up-to-date English file ###
Download [this](http://code.google.com/p/ogameandroid/source/browse/trunk/res/values/strings.xml) file. Everything contained in this file should be translated.

### 3) Translate the file ###
If you got a translated file from Step 1 you can update that file.
If you haven't than just copy the file from Step 2 and translate every string in it.

### 4) Saving the translation ###
Since some languages contain special characters it is very important that you save your translation using UTF-8 encoding.

The default Windows Notepad can't to this so please install a better text-editor (eg. Programmer's Notepad - _Just press File -> Encoding to change to UTF-8_).

### 5) Sending in the translation ###
Send the translated file to phelipebf@gmail.com.

Please put "Translation Your-Language ??" into the subject line _(?? being the code from step 1)_ and attache the file.

In the mail you should also include a name (real-life or nick) so i can add you the credits.

You can also tell me if you want to be added to the project so you can update the translation without having to mail it to me (requires GoogleCode Account).

### 6) Done ###
You should get a answer to your mail within days. The translation should then be updated to the svn-repository and will be included into the next release.