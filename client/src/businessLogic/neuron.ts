import {Data} from './data'

export interface Node {
    props: Record<string, string>
}

export interface Link {
    type: string
    from: Data
    to: Data
}

export interface Neuron {
    data: Data,
    nodes: Record<string, Node>
    links: Link[]
}
