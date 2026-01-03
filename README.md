I think I am now officially hooked on playing different versions of wordle in Afrikaans and English. So I needed a program to help out because I do not like the online tools you get and also, as far as I know, they aren't available for Afrikaans. So I wrote this program in Java.

If Java is installed on your computer you should be able to just double click on the `.jar` file.

Word dictionaries are `.wrdl` files that should be in the same 
directory as the `.jar `file. The file name should be descriptive 
of the language and word length of the words in the file. The file
should start with the two letter ISO code of the language followed
by and underscore and then a number indicating the length of the
words in the file. This will allow the program to automatically
adjust for the word length. If any of this is wrong the program
will default to `en_5.wrdl`
