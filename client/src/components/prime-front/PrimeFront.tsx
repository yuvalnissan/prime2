import * as React from 'react';
import {Data} from '../data/Data';
import styles from './PrimeFront.module.scss';

export interface PrimeFrontProps {
    className?: string;
}

export const PrimeFront = ({ className }: PrimeFrontProps) => {
    return <div className={`${styles.root} ${className} ${styles.all}`}>
        <div className={styles['left-panel']}>
            <button>Button</button>
        </div>
        <div className={styles['right-panel']}>
            <Data/>
        </div>
    </div>;
};
