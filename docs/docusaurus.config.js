module.exports = {
  title: 'Adama Language',
  tagline: 'Single File Infrastructure',
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
          to: 'docs/why-01',
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
          title: 'Docs',
          items: [
            {
              label: 'Style Guide',
              to: 'docs/doc1',
            },
            {
              label: 'Second Doc',
              to: 'docs/doc2',
            },
          ],
        },
        {
          title: 'Community',
          items: [
            {
              label: 'Discord',
              href: 'https://discord.gg/9Ngzr8v',
            },
          ],
        },
        {
          title: 'Social',
          items: [
            {
              label: 'Blog',
              to: 'blog',
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
