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
      items: [
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
              label: 'RSS',
              to: 'http://www.adama-lang.org/blog/rss.xml',
            },
            {
              label: 'Atom',
              to: 'http://www.adama-lang.org/blog/atom.xml',
            },
            {
              label: 'GitHub',
              href: 'https://github.com/mathgladiator/adama-lang',
            }
          ],
        },
      ],
      copyright: `Copyright ©${new Date().getFullYear()} Jeffrey M. Barber. <br/><br/> This disclaimer informs readers (such as yourself) that the views, thoughts, and opinions expressed in the text and images belong solely to the author, and not necessarily to the author's employer, organization, committee or other group or individual. I say crazy shit all on my own.`,
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
        blog: {
          feedOptions: {
            type: 'all',
            copyright: `Copyright © ${new Date().getFullYear()} Jeffrey M. Barber, Inc.`,
          },
        },
      },      
    ],
  ],
};
