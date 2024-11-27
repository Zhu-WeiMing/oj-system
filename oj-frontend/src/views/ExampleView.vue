<template>
  <div class="box">
    <div ref="vantaRef" style="width: 100vw; height: 100vh"></div>
    <div class="banner">
      <h1>OJ Problem Determination System</h1>
      <p>author：zwm</p>
    </div>
  </div>
</template>

<script>
import * as THREE from "three";
import NET from "vanta/src/vanta.net";

export default {
  mounted() {
    this.vantaEffect = NET({
      el: this.$refs.vantaRef,
      THREE: THREE,
      mouseControls: true,
      touchControls: true,
      gyroControls: false,
      minHeight: 200.0,
      minWidth: 200.0,
      scale: 1.0,
      scaleMobile: 1.0,
      color: 0xc7d1e8,
      backgroundColor: 0x400bb1,
      points: 13.0,
      maxDistance: 21.0,
      spacing: 16.0,
    });
  },
  beforeUnmount() {
    if (this.vantaEffect) {
      this.vantaEffect.destroy();
    }
  },
};
</script>

<style lang="less" scoped>
// 确保 CSS 高效，避免使用复杂的选择器或过多的层叠样式
.box {
  position: relative;
  height: 100vh;

  .banner {
    z-index: 999;
    position: absolute;
    top: 10%;
    left: 10%;
    color: #fff;
    // 确保文本和背景的对比度足够，避免使用半透明背景
  }
}

h1 {
  font-size: 66px;
  // 考虑使用更高效的字体，或者减少字体的复杂度
}

p {
  margin-top: 60px;
  font-size: 18px;
  // 避免使用过多的文本阴影或其他可能影响性能的样式
}
</style>
