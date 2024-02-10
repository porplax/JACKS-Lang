<p align="center">
  <img src="https://u.cubeupload.com/ihavecandy/rszjackstext.png">
  
</p>


## About
JACKS is an abandoned project I worked on during November of 2022. It is outdated and (probably) doesn't work well anymore.  While I am not involved anymore, Feel free to look at the code! 

```
jacks file.jk out.class
```
---

# JACKS <sup><sub>**Another (JVM) toy language.**</sub></sup>
```
$ Welcome, This is the first JACKS compiler!
$ Variables and basic functions are the only commands that are implemented.
$ In other words, the only commands that are implemented are 'puts' and 'concat'.

$ 'puts' will print a anything that is provided in the call.
$ 'concat' will concatenate multiple types of objects.

$ I hope you'll enjoy using the JACKS compiler.
$ Check out other compilers made by people like me!

sticks: int = 5 + 5 $ This statement will create a variable.
stones: int = sticks * 2
bones: int = sticks + stones

msg: str = concat(sticks, ' ', stones, ' ', bones) $ Concatenate the 3 variables...
puts(msg) $ Output them!
```
JACKS is influenced by Python, Fortran, and Rust, which makes it easy to learn. 

- Interoperable with Java classes
- No super classes or main methods (it's already programmed in).
- No terminators (;) required.

It utilizes **ASM** for class generation and has it's own parser and lexer. 

 
