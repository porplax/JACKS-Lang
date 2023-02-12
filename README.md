<p align="center">
  <img src="https://u.cubeupload.com/ihavecandy/rszjackstext.png">
</p>

```
jacks file.jak out.class
```
---

# JACKS <sup><sub>**Yes. Another (JVM) toy language.**</sub></sup>
```
$> JACKS PROGRAM (This is a comment btw)

def foo(a: int, b: int) -> int
    return a + b
end def

def bar(a: int) -> int
    return a * 2
end def

bar(foo(4, 4))
```
JACKS is influenced by Python, Fortran, and Rust, which makes it easy to learn. 

- Interoperable with Java classes
- No super classes or main methods.
- No terminators (;) required.

It utilizes **ASM** for class generation and it's own parser and lexer. 


## About
JACKS is nothing more than a generic toy language, made for fun. It has no uses that it specializes in.
It can be very rocky when it comes to parsing and generation, and has no way of effciently telling scopes.

So should you use JACKS? I dunno :P, it can be a great tool for creating projects like a simple web server, visualization, and such.


# TODO
- [ ] Condition statements.
- [ ] Class generation optimization.
- [ ] IO statements.
- [ ] Refactor semantics analysis.
- [ ] Short documentation.
