module.exports = {
  plugins: [
   require('@tailwindcss/typography'),
   require('@tailwindcss/forms'),
   require('@tailwindcss/line-clamp')],
  theme: {
    extend: {
      typography: {
        DEFAULT: {
          css: {
            maxWidth: '120ch',
          }
        }
      },
      colors: {
        white: '#FFFFFF',
        black: '#000000',
        blue: '#1B9AAA',
      },
      flexGrow: {
        2: '2',
        3: '3',
      },
    },
  },
};
