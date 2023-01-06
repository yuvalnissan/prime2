import {Data} from './data'

export interface Node {
    props: Record<string, string>
}

export interface Neuron {
    data: Data,
    nodes: Record<string, Node>
}
