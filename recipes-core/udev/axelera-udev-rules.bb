DESCRIPTION = "Axelera configuration files for udev."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI = " \
	file://72-axelera.rules \
"

inherit allarch
S = "${WORKDIR}"

RULE_FILENAME = "72-axelera.rules"

do_install[nostamp] = "1"
do_unpack[nostamp] = "1"   

do_install() {
	install -d ${D}${nonarch_base_libdir}/udev/rules.d
	install -m 0644 ${WORKDIR}/72-axelera.rules ${D}${nonarch_base_libdir}/udev/rules.d/72-axelera.rules
}

FILES_${PN} = "${nonarch_base_libdir}/udev/rules.d/${RULE_FILENAME}"
