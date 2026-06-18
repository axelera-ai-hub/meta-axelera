SUMMARY = "Axelera PCIe kernel driver"
DESCRIPTION = "Kernel driver for Axelera AI accelerator PCIe devices"

HOMEPAGE = "https://github.com/axelera-ai-hub/axelera-driver"
BUGTRACKER = "https://github.com/axelera-ai-hub/axelera-driver/issues"
SECTION = "kernel"
CVE_PRODUCT = "axelera"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4641e94ec96f98fabc56ff9cc48be14b"

inherit module

PV = "1.7.0"

SRC_URI = "git://git@github.com/axelera-ai-hub/axelera-driver.git;protocol=ssh;branch=release/v1.7"
SRCREV = "ec85b379c03c079c8fd25e3e766e511e78b1b481"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "KDIR=${STAGING_KERNEL_BUILDDIR}"

do_install:append() {
    install -d ${D}${nonarch_base_libdir}/udev/rules.d
    install -m 0644 ${S}/udev/72-axelera.rules ${D}${nonarch_base_libdir}/udev/rules.d/72-axelera.rules
}

FILES:${PN} += "${nonarch_base_libdir}/udev/rules.d/72-axelera.rules"
