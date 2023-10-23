<p align="center">
  <img src="https://u.cubeupload.com/ihavecandy/rszjackstext.png">
  
</p>

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


## About
JACKS is nothing more than a generic toy language, made for fun. It has no uses that it specializes in.
So should you use JACKS? I dunno :P, it can be a great tool for creating projects like a simple web server, visualization, and such.


# TODO
- [ ] Condition statements.
- [ ] Class generation optimization.
- [ ] IO statements.
- [X] Refactor semantics analysis.
- [ ] Short documentation.
