DESCRIPTION = "Linux kernel for ${MACHINE}"
SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"
VER ?= "${@bb.utils.contains('TARGET_ARCH', 'aarch64', '64' , '', d)}"

KERNEL_VERSION = "4.4.35"

SRCDATE = "20180301"
COMPATIBLE_MACHINE = "(hd60)"

inherit kernel machine_kernel_pr

SRC_URI[arm.md5sum] = "bb368255800be3d3d7cfa2710928fe9c"
SRC_URI[arm.sha256sum] = "3dd7e7a99f70f0be8b725e4628f243c3aa1d42072a32e4a4b5268f69b535fc1d"

SRC_URI = "http://downloads.mutant-digital.net/linux-${PV}-${SRCDATE}-${ARCH}.tar.gz;name=${ARCH} \
	file://defconfig \
	file://0002-log2-give-up-on-gcc-constant-optimizations.patch \
	file://0003-dont-mark-register-as-const.patch \
"

# By default, kernel.bbclass modifies package names to allow multiple kernels
# to be installed in parallel. We revert this change and rprovide the versioned
# package names instead, to allow only one kernel to be installed.
PKG_${KERNEL_PACKAGE_NAME}-base = "kernel-base"
PKG_${KERNEL_PACKAGE_NAME}-image = "kernel-image"
RPROVIDES_PKG_${KERNEL_PACKAGE_NAME}-base = "kernel-${KERNEL_VERSION}"
RPROVIDES_PKG_${KERNEL_PACKAGE_NAME}-image = "kernel-image-${KERNEL_VERSION}"

S = "${WORKDIR}/linux-${PV}"

export OS = "Linux"
KERNEL_OBJECT_SUFFIX = "ko"
KERNEL_IMAGETYPE = "uImage"
KERNEL_OUTPUT = "arch/${ARCH}/boot/${KERNEL_IMAGETYPE}"

FILES_${KERNEL_PACKAGE_NAME}-image = "/tmp"

kernel_do_install_append() {
        install -d ${D}/tmp
        install -m 0755 ${KERNEL_OUTPUT} ${D}/tmp
}

pkg_postinst_kernel-image() {
	if [ "x$D" == "x" ]; then
		if [ -f /tmp/${KERNEL_IMAGETYPE} ] ; then
			dd if=/tmp/${KERNEL_IMAGETYPE} of=/dev/mmcblk0p20
		fi
	fi
	true
}