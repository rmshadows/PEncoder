#!/usr/bin/env bash
# =============================================================================
# PEncoder Linux 本地原生镜像构建脚本
# =============================================================================
# 用于在 CI 未覆盖的 Linux 架构（如特定 arm64/ppc64le/loongarch 等）上，
# 由用户本机安装 GraalVM/NIK 后手动执行此脚本生成可执行文件。
#
# 使用前请先填写下方「用户可配置变量」。
# 依赖：GraalVM 或 Liberica Native Image Kit（需含 native-image），
#      以及 AWT 所需的 X11 开发库（见脚本内说明）。
# =============================================================================
set -e

# -----------------------------------------------------------------------------
# 用户可配置变量（请直接修改下面几行的等号右边）
# -----------------------------------------------------------------------------
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# 【必填】GraalVM / Liberica NIK 安装目录（必须含 bin/native-image）
GRAALVM_HOME="/usr/lib/jvm/graalvm-ce-java25"

# 【可选】是否先执行 Maven 打 JAR：true=先 mvn package，false=使用已有 target/pencoder.jar
BUILD_JAR="true"

# 【可选】产出目录（可执行文件与 run.sh 将生成在此目录）
OUTPUT_DIR="$SCRIPT_DIR/dist"

BUILD_DIR="$SCRIPT_DIR"
export GRAALVM_HOME

# -----------------------------------------------------------------------------
# 无需修改以下内容（除非你知道在做什么）
# -----------------------------------------------------------------------------
JAR_PATH="$BUILD_DIR/target/pencoder.jar"
MODULE_MAIN="cn.rmshadows.PEncoderModule/appLauncher.PEncoderGUILauncher"

echo "=============================================="
echo "PEncoder 本地 Native Image 构建 (Linux)"
echo "=============================================="
echo "GRAALVM_HOME = $GRAALVM_HOME"
echo "BUILD_JAR    = $BUILD_JAR"
echo "BUILD_DIR    = $BUILD_DIR"
echo "OUTPUT_DIR   = $OUTPUT_DIR"
echo "=============================================="

# 检查 GraalVM 目录与 native-image
if [ ! -d "$GRAALVM_HOME" ]; then
  echo "错误: GRAALVM_HOME 指向的目录不存在: $GRAALVM_HOME"
  echo "请编辑本脚本顶部，将 GRAALVM_HOME 设为你的 GraalVM/NIK 安装路径。"
  exit 1
fi
export JAVA_HOME="$GRAALVM_HOME"
export PATH="$JAVA_HOME/bin:$PATH"

if ! command -v native-image >/dev/null 2>&1; then
  echo "错误: 未找到 native-image（$GRAALVM_HOME/bin/native-image）。"
  echo "请使用带 native-image 的 GraalVM 或 Liberica Native Image Kit。"
  echo "GraalVM 社区版可执行: gu install native-image"
  exit 1
fi

# 提示 AWT/X11 依赖（不强制安装，避免无 sudo 环境报错）
echo ""
echo "若构建或运行时报 AWT/X11 相关错误，请安装开发库（按发行版调整）："
echo "  Debian/Ubuntu: sudo apt-get install -y libx11-dev libxtst-dev libxi-dev libxrender-dev libxext-dev libxrandr-dev libfreetype6-dev libfontconfig1-dev"
echo "  Fedora:        sudo dnf install libX11-devel libXtst-devel libXi-devel libXrender-devel libXext-devel libXrandr-devel freetype-devel fontconfig-devel"
echo "  openSUSE:      sudo zypper in libX11-devel libXtst-devel libXi-devel libXrender-devel libXext-devel libXrandr-devel freetype2-devel fontconfig-devel"
echo ""

# 可选：先构建 JAR
if [ "$BUILD_JAR" = "true" ]; then
  echo ">>> 执行 Maven 打包..."
  cd "$BUILD_DIR"
  mvn -B package -DskipTests
  cd - >/dev/null
  echo ">>> JAR 构建完成: $JAR_PATH"
else
  if [ ! -f "$JAR_PATH" ]; then
    echo "错误: 未找到 JAR 文件: $JAR_PATH"
    echo "请先执行 mvn package 或设置 BUILD_JAR=true 让本脚本自动打 JAR。"
    exit 1
  fi
  echo ">>> 使用已有 JAR: $JAR_PATH"
fi

# 在 BUILD_DIR 下执行 native-image（与 CI 一致，产出 PEncoder 与 .so 在当前目录）
echo ""
echo ">>> 执行 native-image（可能需要数分钟）..."
WORK_DIR="$BUILD_DIR"
cd "$WORK_DIR"

native-image \
  --module-path "$JAR_PATH" \
  -m "$MODULE_MAIN" \
  -o PEncoder \
  --no-fallback \
  -Djava.awt.headless=false \
  -H:IncludeResources='appCtrl/.*\.properties' \
  -H:IncludeResources='.*Messages_.*\.properties'

echo ">>> native-image 完成。"

# 收集产物：可执行文件 + .so → OUTPUT_DIR，并生成 run.sh
mkdir -p "$OUTPUT_DIR" "$OUTPUT_DIR/lib"
cp -f "$WORK_DIR/PEncoder" "$OUTPUT_DIR/"
chmod +x "$OUTPUT_DIR/PEncoder"
cp -n "$WORK_DIR"/*.so "$OUTPUT_DIR/lib/" 2>/dev/null || true

cat > "$OUTPUT_DIR/run.sh" << 'RUN'
#!/usr/bin/env bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
export LD_LIBRARY_PATH="${SCRIPT_DIR}/lib:${LD_LIBRARY_PATH}"
exec "${SCRIPT_DIR}/PEncoder" "$@"
RUN
chmod +x "$OUTPUT_DIR/run.sh"

echo ""
echo "=============================================="
echo "构建完成。产出目录: $OUTPUT_DIR"
echo "  - PEncoder    可执行文件"
echo "  - lib/*.so    AWT 等动态库"
echo "  - run.sh      启动脚本（请用 ./run.sh 运行，勿直接 ./PEncoder）"
echo "=============================================="
