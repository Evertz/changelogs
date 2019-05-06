## Changelog Compiler

The changelog compiler is a tool used for compiling changelogs into various formats for distribution
to various platforms. 
It consumes a generic, markdown based human readable changelog input format that is easy to write and maintain.
It also allows for automatic SemVer increments, taking the largest increment and applying it to a base version number.

### What it does not do
The following is currently outside the scope of what the changelog compiler was design to do

* Generate changelogs from SCM commit history
* Modify pre-generated changelogs
* Export / post changelog data to a 3rd party system

### Changelog input format
The compiler accepts incoming changelog entries as markdown files, with a semi-strict formatting.
Ideally, each changelog entry should be contained within its own file, but this isn't strictly necessary
or enforced.

Below is an example of a changelog input, the files name or location is irrelevant

```
### SCOPE

* increment __type__[ticket #](): note
```

#### Terms
__scope__: The application area that's associated with this change

__increment__: The increment amount that should be applied when calculating the resulting version number

__type__: A change type associated with this change (eg, _fix_, _feat_ etc)

__ticket__: (optional) An issue tracker ticket number, will be automatically linked

__note__: The final changelog note used for this change

#### Example

```
### Changelog Compiler

* minor __fix__[DEV-1234](): ensure all changes are validated when compiling

```

The note may contain further formatting, but this may be removed based on the output format

### Changelog output formats
The compiler supports a number of different output formats, they are listed below.

* __Markdown__: outputs the changelogs as a markdown file
* __HTML__: outputs the changelogs as a self contained HTML file
* __Debian__: outputs the changelogs as a debian changelog file
* __Protobuf__: outputs the changelogs as the internal protobuf format, this format is the required input format for
some functions

### Usage
The compiler can be consumed two ways, via a Bazel macro or directly via the command line. Both ways require a `changelog.conf` file
Multiple configurations can be defined in one file. The configuration is in text protobuf format.

```
configurations {
  key: "default"
  value {
    name: "default"
    emitterFlags {
      ticketBaseUrl: "https://go/jira/browse/"
      project: "Changelog"
      owner: "Evertz R&D"
      owner_email: "opensourcev@evertz.com"
    }
    validatorFlags {
      scopes: "changelog"
      types: "feat"
      types: "fix"
      types: "build"
      types: "chore"
      types: "style"
      types: "refactor"
      increments: "major"
      increments: "minor"
      increments: "patch"
      allowBlankTicket: True
    }
  }
}
```

#### Usage via command line
The compiler requires Java to be installed, invoke with `java -jar changelog_deploy.jar [command] --help` for more info on each command

```
positional arguments:
  command
    compile              Compiles a set of changelog entries into a single changelog file
    regen                Regenerate an archived changelog and emits it in the given format
    merge                Merges changelog entry sets into a changelog archive

named arguments:
  -h, --help             show this help message and exit
```

__Note__: When using the compiler via the command line, the consumer is responsible for moving output files into the expected
locations. Also note, that due to platform issues, it's recommenced that when compiling a large set of changelogs,
the list of files be passed to the compiler as a args file, for example `java -jar changelog_deploy.jar compile @args.txt`,
each line is treated as an arg.

