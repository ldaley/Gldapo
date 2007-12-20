log.info "Updating version numbers in download.html"
download_html = new File("${project.basedir}/target/site/download.html")
contents = download_html.text
writable = download_html as Writable
writable.write(contents.replaceAll('PROJECT_VERSION', project.version))

def download_dir = "${project.basedir}/target/site/download"
log.info "Removing download dir"
ant.delete(dir: download_dir, failonerror: false)

log.info "Making download dir"
ant.mkdir(dir: download_dir) 

log.info "Copying standalone zip to /download"
ant.copy(todir: download_dir) {
	fileset(dir: "${project.basedir}/target") {
		include(name: "gldapo-${project.version}-standalone.zip")
	}
}