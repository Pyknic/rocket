# Rocket
Launch system that allows classes to be invoked in phases to make sure all dependencies are satisfied.

## Features
* Custom phases
* Builder-style

## Installation

#### Using Maven
Add the following to your `pom.xml`-file:

```xml
<dependency>
    <groupId>com.github.pyknic</groupId>
    <artifactId>rocket</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Using Gradle
Add the following to your `build.gradle`-file:
```gradle
compile group: 'com.github.pyknic', name: 'rocket', version: '1.0.0'
```

## Usage
The launch system follows a builder pattern where the two interfaces `Rocket` and `RocketBuilder` are central.

### Configuration
The builder pattern allows Rocket to be built by appending types available for launching. If the dependency graph is incomplete or contains cyclic dependencies when the `RocketBuilder.build()`-method is invoked, then an `RocketException` is thrown.

```java
// Create a new Launcher with a number of instances.
Rocket rocket = Rocket.builder(Phase.class)
    .with(foo)
    .with(bar)
    .with(baz)
    .build();
```

### Define Phases
Phases are defined by creating an `enum` class and passing it to the `Rocket.builder(...)`-method.

```java
// Define an enum with three phases.
enum Phase {
    INIT,
    UPDATE,
    DESTROY
}
```

### Add Action
Actions can be added to a class by adding the `@Execute`-annotation to the method. If the method takes any parameters, they will be injected automatically. Methods with the same phase will be invoked in a order that guarantees that all the arguments have already passed that stage.

```java
class ExampleComponent {

    @Execute("init")
    void onInit() {
        ...
    }
    
    @Execute("update")
    void onUpdate(OtherComponent other) {
        ...
    }

}
```

## License
Copyright 2017 Emil Forslund

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
