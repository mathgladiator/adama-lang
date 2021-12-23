import React from 'react';
import classnames from 'classnames';
import Layout from '@theme/Layout';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import useBaseUrl from '@docusaurus/useBaseUrl';
import styles from './styles.module.css';

const features = [
  {
    title: <>Single File Infrastructure</>,
    imageUrl: 'img/infra.jpg',
    description: (
      <>
        A single file is responsible for all the infrastructure to connect players together in a durable and stateful board game experience. It is like a whole slew of AWS services (like EC2, Lambda, S3, SWF, SQS, DynamoDB, and RDS) all in a single package.
      </>
    ),
  },
  {
    title: <>Dungeon-Master as a Service</>,
    imageUrl: 'img/dm.jpg',
    description: (
      <>
        Adama allows you to perform complex transactions across a variety of connected players. You no longer manipulate and query data, data manipulates and queries you! This changes the game in modelling the complex interactions between people that emerge in complex board games.
      </>
    ),
  },
  {
    title: <>Living Documents</>,
    imageUrl: 'img/alive.jpg',
    description: (
      <>
        Documents are no longer dead bytes sitting around. Instead, they are alive and expressive automatons with whatever will you wish them to have. The combination of storage and compute yields an exceptionally tiny new server model.
      </>
    ),
  },
];

function Feature({imageUrl, title, description}) {
  const imgUrl = useBaseUrl(imageUrl);
  return (
    <div className={classnames('col col--4', styles.feature)}>
      {imgUrl && (
        <div className="text--center">
          <img className={styles.featureImage} src={imgUrl} alt={title} />
        </div>
      )}
      <h3>{title}</h3>
      <p>{description}</p>
    </div>
  );
}

function Home() {
  const context = useDocusaurusContext();
  const {siteConfig = {}} = context;
  return (
    <Layout
      title={`${siteConfig.title}`}
      description="Programming Language for Online Board Games">
      <header className={classnames('hero hero--primary', styles.heroBanner)}>
        <div className="container">
          <h1 className="hero__title">{siteConfig.title}</h1>
          <p className="hero__subtitle">{siteConfig.tagline}</p>
          <div className={styles.buttons}>
            <Link
              className={classnames(
                'button button--outline button--secondary button--lg',
                styles.getStarted,
              )}
              to={useBaseUrl('docs/why-the-origin-story')}>
              Learn more...
            </Link>
          </div>
        </div>
      </header>
      <main>
        {features && features.length && (
          <section className={styles.features}>
            <div className="container">
              <div className="row">
                {features.map((props, idx) => (
                  <Feature key={idx} {...props} />
                ))}
              </div>
            </div>
          </section>
        )}
      </main>
    </Layout>
  );
}

export default Home;
