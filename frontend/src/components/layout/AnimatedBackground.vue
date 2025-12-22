<template>
  <div class="animated-background">
    <div class="circle-container">
      <div class="circle"></div>
      <div class="circle"></div>
      <div class="circle"></div>
      <div class="circle"></div>
      <div class="circle"></div>
    </div>
  </div>
</template>

<script setup>
// This component provides a subtle animated background for login/register pages
</script>

<style scoped>
.animated-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  overflow: hidden;
  z-index: -1;
}

.circle-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 15s infinite linear;
}

.circle:nth-child(1) {
  width: 300px;
  height: 300px;
  top: 10%;
  left: 5%;
  animation-duration: 20s;
}

.circle:nth-child(2) {
  width: 150px;
  height: 150px;
  top: 20%;
  right: 10%;
  animation-duration: 25s;
  animation-direction: reverse;
}

.circle:nth-child(3) {
  width: 200px;
  height: 200px;
  bottom: 15%;
  left: 15%;
  animation-duration: 30s;
}

.circle:nth-child(4) {
  width: 100px;
  height: 100px;
  bottom: 25%;
  right: 20%;
  animation-duration: 22s;
  animation-direction: reverse;
}

.circle:nth-child(5) {
  width: 250px;
  height: 250px;
  top: 40%;
  left: 40%;
  animation-duration: 35s;
}

@keyframes float {
  0% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(100px, 100px) rotate(90deg);
  }
  50% {
    transform: translate(200px, 0) rotate(180deg);
  }
  75% {
    transform: translate(100px, -100px) rotate(270deg);
  }
  100% {
    transform: translate(0, 0) rotate(360deg);
  }
}
</style>