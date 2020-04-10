module.exports = {
  title: 'Adama Language',
  tagline: 'A Programming Language for Board Games... and more... maybe?',
  url: 'https://adama-lang.org',
  baseUrl: '/',
  favicon: 'img/favicon.ico',
  organizationName: 'mathgladiator',
  projectName: 'adama-lang', // Usually your repo name.
  themeConfig: {
    navbar: {
      title: 'Adama Language Documentation',
      logo: {
        alt: 'The Goat Adama',
        src: 'img/adama-height-196.jpg',
      },
      links: [
        {
          to: 'docs/why-the-origin-story',
          activeBasePath: 'docs',
          label: 'Documents',
          position: 'left',
        },
        {to: 'blog', label: 'Updates', position: 'left'},
        {
          href: 'https://github.com/mathgladiator/adama-lang',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'The Documents',
          items: [
            {
              label: 'Why',
              to: 'docs/why-the-origin-story',
            },
            {
              label: 'What',
              to: 'docs/what-the-living-document',
            },
            {
              label: 'How',
              to: 'docs/how-devkit-install',
            },
            {
              label: 'Details',
              to: 'docs/details-types',
            },
          ],
        },
        {
          title: 'Links',
          items: [
            {
              label: 'Blog',
              to: 'blog',
            },
            {
              label: 'Discord',
              href: 'https://discord.gg/9Ngzr8v',
            },
            {
              label: 'GitHub',
              href: 'https://github.com/mathgladiator/adama-lang',
            }
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} Jeffrey M. Barber.`,
    },
  },
  presets: [
    [
      '@docusaurus/preset-classic',
      {
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      },
    ],
  ],
};
