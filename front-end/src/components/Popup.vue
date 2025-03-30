<template>
    <div v-if="visible" class="popup-overlay">
      <div class="popup">
        <div class="popup-header">
          <button @click="close" class="close-button">×</button>
        </div>
        <div class="popup-content">
          <slot></slot>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
    },
    emits: ['close'],
    methods: {
      close() {
        this.$emit('close');
      },
    },
  };
  </script>
  
  <style scoped>
  .popup-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
  }
  
  .popup {
    background-color: white;
    padding: 20px;
    border-radius: 5px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    width: 80%;
    max-width: 500px;
    position: relative; /* Needed for absolute positioning of close button */
  }
  
  .popup-header {
    display: flex;
    justify-content: flex-end;
    padding-bottom: 10px; /* Add space between the header and content */
  }
  
  .close-button {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    position: absolute;
    top: 10px;
    right: 10px;
  }
  
  .popup-content {
    /* Add any styling for the content within the popup */
  }
  </style>
  