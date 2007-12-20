log.info "Updating version numbers in download.html"
download_html = new File("${project.basedir}/target/site/download.html")
contents = download_html.text
writable = download_html as Writable
writable.write(contents.replaceAll('PROJECT_VERSION', project.version))

log.info "Removing download/*"
ant.delete() {
	fileset(dir: "${project.basedir}/target/site/download", includes: "**/*") 
}

log.info "Copying standalone zip to /download"
ant.copy(todir: "${project.basedir}/target/site/download") {
	fileset(dir: "${project.basedir}/target") {
		include(name: "gldapo-${project.version}-standalone.zip")
	}
}