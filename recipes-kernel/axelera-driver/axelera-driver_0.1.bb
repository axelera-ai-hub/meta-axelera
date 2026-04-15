SUMMARY = "Axelera PCIe kernel driver"
DESCRIPTION = "Kernel driver for Axelera AI accelerator PCIe devices"

HOMEPAGE = "https://github.com/axelera-ai-hub/axelera-driver"
BUGTRACKER = "https://github.com/axelera-ai-hub/axelera-driver/issues"
SECTION = "kernel"
CVE_PRODUCT = "axelera"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4641e94ec96f98fabc56ff9cc48be14b"

inherit module

PV = "1.6.0"

SRC_URI = "git://git@github.com/axelera-ai-hub/axelera-driver.git;protocol=ssh;branch=release/v1.6
           file://0001-Fix-kernel-5.4-compatibility.patch \
"
SRCREV = "a98a609514b16cf2e0ff749d5676bbaf5bde93aa"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "KDIR=${STAGING_KERNEL_BUILDDIR}"
