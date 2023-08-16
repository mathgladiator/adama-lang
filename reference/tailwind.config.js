module.exports = {
  plugins: [
   require('@tailwindcss/typography'),
   require('@tailwindcss/forms'),
   require('@tailwindcss/line-clamp')],
  theme: {
    extend: {
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
