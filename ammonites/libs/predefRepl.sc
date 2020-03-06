import $exec.`predef`

ammonite.shell.Configure(interp, repl, wd)
interp.configureCompiler(_.settings.nowarn.value = false)
