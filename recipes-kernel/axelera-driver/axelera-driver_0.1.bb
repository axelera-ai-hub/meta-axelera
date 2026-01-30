SUMMARY = "Axelera PCIe kernel driver"
DESCRIPTION = "Kernel driver for Axelera AI accelerator PCIe devices"

HOMEPAGE = "https://github.com/axelera-ai-hub/axelera-driver"
BUGTRACKER = "https://github.com/axelera-ai-hub/axelera-driver/issues"
SECTION = "kernel"
CVE_PRODUCT = "axelera"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4641e94ec96f98fabc56ff9cc48be14b"

inherit module

PV = "1.5.3"

SRC_URI = "git://git@github.com/axelera-ai-hub/axelera-driver.git;protocol=ssh;branch=release/v1.5"
SRCREV = "1216e9b049fdd92fd0124c2106d3b2ae10fe163d"

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "KDIR=${STAGING_KERNEL_BUILDDIR}"
