![Axelera AI Banner](Ax_Banner.png)

# meta-axelera

Yocto layer for Axelera AI hardware integration

## Table of Contents

- [Overview](#overview)
  - [VoyagerSDK Runtime Requirements](#voyagersdk-runtime-requirements)
- [Dependencies](#dependencies)
  - [Layer Dependencies](#layer-dependencies)
  - [Kernel Requirements](#kernel-requirements)
- [Supported Yocto Versions](#supported-yocto-versions)
- [Quick Start](#quick-start)
- [Layer Contents](#layer-contents)
- [Performance Optimization](#performance-optimization)
- [Customization](#customization)
- [Troubleshooting](#troubleshooting)

## Overview

This layer provides the kernel driver and udev rules for Axelera AI PCIe accelerator devices on Yocto-based Linux distributions. It enables hardware detection and communication with Axelera AI accelerators at the kernel level.

### VoyagerSDK Runtime Requirements

The complete VoyagerSDK (inference runtime, model compiler, and development tools) is not currently available natively for Yocto environments and must run inside a Docker container.

#### VoyagerSDK Deployment Architecture

- **Kernel Driver**: Installed natively on your Yocto system (provided by this layer)
- **VoyagerSDK**: Must run inside a Docker container

The kernel driver enables the host system to recognize and communicate with Axelera AI PCIe devices, but all inference workloads and SDK tools must be executed within a containerized VoyagerSDK environment.

#### Getting Started with VoyagerSDK

1. Build and deploy your Yocto image with the `meta-axelera` layer
2. Install Docker on your target system
3. Follow the [VoyagerSDK Docker Setup Guide](https://support.axelera.ai/hc/en-us/articles/25953148201362-Install-Voyager-SDK-in-a-Docker-Container) to set up the VoyagerSDK inside a container

For the complete VoyagerSDK documentation, visit the [VoyagerSDK repository](https://github.com/axelera-ai-hub/voyager-sdk).

## Dependencies

### Layer Dependencies

This layer depends on:
- **URI**: [git://git.yoctoproject.org/poky](https://git.yoctoproject.org/poky)
- **Branch**: scarthgap
- **Layers**: meta, meta-poky, meta-yocto-bsp

### Kernel Requirements

#### DMA Buffer (dmabuf) Support

DMA buffers enable zero-copy data transfer between the CPU and the Axelera AI accelerator, eliminating expensive memory copy operations. **This is a mandatory requirement** for the Axelera AI accelerator to function properly.

Ensure your Linux kernel has DMA-BUF support enabled. The following kernel configurations are required:

```bash
CONFIG_DMABUF_HEAPS=y
CONFIG_DMABUF_HEAPS_SYSTEM=y
```

To enable these in your Yocto build, create a kernel configuration fragment:

```bash
# Create a kernel config fragment
cat << 'EOF' > recipes-kernel/linux/files/dmabuf.cfg
CONFIG_DMABUF_HEAPS=y
CONFIG_DMABUF_HEAPS_SYSTEM=y
EOF
```

Then add it to your kernel recipe by appending to your kernel bbappend file:

```bash
# In recipes-kernel/linux/linux-<your-kernel-version>.bbappend
KERNEL_CONFIG_FRAGMENTS:append = " file://dmabuf.cfg"
```

##### Verification

After building and booting your image, verify DMA-BUF support is enabled:

```bash
# Verify DMA heap devices exist
ls -l /dev/dma_heap/

# If the directory exists and contains system heap, dmabuf is properly configured
```

## Supported Yocto Versions

This branch supports **Yocto 5.0 (Scarthgap)** only.

For other Yocto versions, check out the corresponding branch:
- `yocto/scarthgap` - Yocto 5.0 (Scarthgap) ← You are here
- `yocto/kirkstone` - Yocto 4.0 (Kirkstone)
- `yocto/dunfell` - Yocto 3.1 (Dunfell)

## Quick Start

### Prerequisites

- A working Yocto build environment (see [Yocto Project Quick Build](https://docs.yoctoproject.org/brief-yoctoprojectqs/index.html))
- Scarthgap-based Yocto distribution

### 1. Clone the Layer

Add this layer to your Yocto project:

```bash
# Navigate to your Yocto sources directory
cd <yocto-project>/sources

# Clone meta-axelera (scarthgap branch)
git clone -b yocto/scarthgap https://github.com/axelera-ai/meta-axelera.git
```

### 2. Add the Layer to Your Build

```bash
# Navigate to your build directory
cd <yocto-project>/build

# Source the environment if not already done
source ../poky/oe-init-build-env

# Add meta-axelera layer
bitbake-layers add-layer ../sources/meta-axelera

# Verify the layer was added
bitbake-layers show-layers
```

### 3. Add Axelera Packages to Your Image

Edit `conf/local.conf` to include Axelera packages:

```bash
# Add Axelera packages to your image
IMAGE_INSTALL:append = " axelera-driver"
```

### 4. Build Your Image

```bash
# Build your target image
bitbake <your-image-recipe>

# Example:
bitbake core-image-minimal
```

## Layer Contents

This layer provides the following recipes:

### axelera-driver
- **Description**: PCIe kernel driver for Axelera AI accelerators, including udev rules for device detection and configuration
- **Type**: Kernel module
- **Location**: `recipes-kernel/axelera-driver/`
- **Package name**: `axelera-driver`
- **Installs**: kernel module + `/lib/udev/rules.d/72-axelera.rules`

### Package Format

This layer configures builds to use `.deb` packages by default for easier distribution and installation. Packages will be available at:

```
build/tmp/deploy/deb/core2-64/axelera-driver_*.deb
```

To use a different package format (RPM or IPK), override in your `conf/local.conf`:

```bash
PACKAGE_CLASSES = "package_rpm"  # For RPM packages
# or
PACKAGE_CLASSES = "package_ipk"  # For IPK packages
```

## Performance Optimization

Optimizing your system configuration can significantly improve inference performance and reduce latency when using Axelera AI accelerators.

### Hardware Acceleration for Pre/Post-Processing

For optimal performance in computer vision pipelines, leverage available GPU acceleration libraries on your host platform for pre-processing and post-processing operations. The Axelera AI accelerator handles inference, while the host GPU can efficiently handle image transformations before and after inference.

#### GPU Acceleration Libraries

Depending on your CPU/SoC platform, integrate the appropriate GPU acceleration library:

- **Rockchip platforms**: `libmali` - ARM Mali GPU acceleration
- **NXP i.MX platforms**: `libimxvpuapi`, `libg2d` - Hardware-accelerated video and 2D graphics
- **Intel platforms**: `intel-media-driver`, `libva` - Intel GPU media acceleration
- **Generic**: `OpenCL`, `Vulkan` - Cross-platform GPU compute APIs

## Customization

### Adding to Your Custom Image Recipe

To include Axelera support in your custom image recipe:

```bitbake
# In your custom-image.bb
IMAGE_INSTALL += " \
    axelera-driver \
"
```

### Optional Packages

For debugging and testing, you may want to add:

```bitbake
IMAGE_INSTALL:append = " \
    pciutils \
    usbutils \
    i2c-tools \
"
```

## Troubleshooting

### Layer Compatibility Error

If you see "Layer meta-axelera is not compatible with the core layer":
- Ensure you're using the correct branch for your Yocto version
- Check `conf/layer.conf` has your Yocto version in `LAYERSERIES_COMPAT_meta-axelera`

### Driver Not Loading

If the kernel module doesn't load automatically:

```bash
# Check if module is installed
ls /lib/modules/$(uname -r)/extra/

# Manually load the module
modprobe metis

# Check module version
cat /sys/class/metis/version

# Check kernel logs
dmesg | grep -i axl
```

### udev Rules Not Applied

If udev rules aren't working:

```bash
# Check if rules are installed
ls -l /lib/udev/rules.d/72-axelera.rules

# Reload udev rules
udevadm control --reload-rules
udevadm trigger
```

### Build Artifacts Location

After a successful build, your images will be in:
```
build/tmp/deploy/images/<machine>/
```

Kernel modules are located at:
```
build/tmp/work/<machine>-poky-linux/axelera-driver/<version>/
```

## Support

For issues, questions, or contributions:
- **Community**: https://community.axelera.ai/
- **GitHub Issues**: https://github.com/axelera-ai/meta-axelera/issues

## License

This layer is licensed under the MIT License. See [COPYING.MIT](COPYING.MIT) for details.

Individual recipes may have different licenses - check the LICENSE field in each recipe.

## Maintainers

- Axelera AI Team <support@axelera.ai>

## References

- [Yocto Project Documentation](https://docs.yoctoproject.org/)
- [Axelera AI SDK](https://github.com/axelera-ai-hub/voyager-sdk)
